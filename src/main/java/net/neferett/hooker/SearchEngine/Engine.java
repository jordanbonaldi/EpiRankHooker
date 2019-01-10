package net.neferett.hooker.SearchEngine;

import lombok.Data;
import net.neferett.hooker.Entity.Student;
import net.neferett.redisapi.Utils.SerializationUtils;
import org.apache.lucene.document.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class Engine {

    private MemoryIndexer indexer;

    private List<Student> students;

    private int max;

    public Engine() {
        this.indexer = new MemoryIndexer();
    }

    public void loadStudents(List<Student> students)
    {
        this.max = students.size();
        this.students = students;

        students.forEach(e ->
            this.indexer.indexDocument("student", e.getTitle())
        );
    }

    private boolean filtering(double index, String sign, double value) {

        return sign.equals(">=") ?  value >= index :
                sign.equals("<=") ?  value <= index :
                        sign.equals(">") ?  value  > index: index < value;
    }

    private List<Student> searchGPA(double index, String sign)
    {
        return this.students.stream().filter(e ->
                this.filtering(index, sign, e.getGpa().get(0).getGpa().toLowerCase().contains("/") ? 0 : Double.valueOf(e.getGpa().get(0).getGpa()))
        ).collect(Collectors.toList());
    }

    private List<Student> searchPromo(int promo)
    {
        return this.students.stream().filter(e ->
                e.getPromo() == promo
        ).collect(Collectors.toList());
    }

    private String returnPattern(String in, String index)
    {
        Pattern pattern = Pattern.compile(index);
        Matcher matcher = pattern.matcher(in);

        if (matcher.find())
            return in.substring(matcher.start());
        return null;
    }



    public List<Student> searchAll(String searchQuery)
    {
        String al;
        List<Student> gpa = new ArrayList<>();
        List<Student> promo = new ArrayList<>();
        List<Student> students = new ArrayList<>();

        if ((al = this.returnPattern(searchQuery, "gpa")) != null) {
            String tab[] = al.split( " ");
            gpa.addAll(this.searchGPA(Double.valueOf(tab[2]), tab[1]));
        }

        if ((al = this.returnPattern(searchQuery, "promo")) != null) {
            String tab[] = al.split( " ");
            promo.addAll(this.searchPromo(Integer.valueOf(tab[1])));
        }

        if ((al = this.returnPattern(searchQuery, "student")) != null) {
            String tab[] = al.split( " ");
            List<Document> documents = this.indexer.searchIndex("body", tab[1].toLowerCase() + (tab.length == 3 ? tab[2].toLowerCase() : ""), this.max);

            students.addAll(documents.stream().map(e -> this.students.stream().filter(a ->
                    a.getTitle().equals(e.get("body"))).findFirst().orElse(null)
            ).collect(Collectors.toList()));
        }

        return this.students.stream().filter(e ->
            (gpa.isEmpty() || gpa.contains(e)) && (promo.isEmpty() || promo.contains(e)) && (students.isEmpty() || students.contains(e))
        ).sorted((a, b) ->
                Double.compare(Double.valueOf(b.getGpa().get(0).getGpa()), Double.valueOf(a.getGpa().get(0).getGpa()))
        ).collect(Collectors.toList());
    }
}
