package net.neferett.hooker.SearchEngine;

import lombok.Data;
import net.neferett.hooker.Entity.City;
import net.neferett.hooker.Entity.Student;
import net.neferett.redisapi.Utils.SerializationUtils;
import org.apache.lucene.document.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class Engine {

    private MemoryIndexer studentIndexer;

    private List<Student> students;

    private int maxStudents;

    public Engine() {
        this.studentIndexer = new MemoryIndexer();
    }

    public void loadStudents(List<Student> students)
    {
        this.maxStudents = students.size();
        this.students = students;

        students.forEach(e ->
            this.studentIndexer.indexDocument("student", e.getTitle())
        );
    }

    private boolean filtering(double index, String sign, double value)
    {
        return sign.equals(">") ? index < value
                : sign.equals("=") ? index == value : index > value;
    }

    private Predicate<Student> searchGPA(double index, String sign)
    {
        return e -> this.filtering(index, sign, e.getGpa().get(0).getGpa().toLowerCase().contains("/") ? 0 : Double.valueOf(e.getGpa().get(0).getGpa()));
    }

    private Predicate<Student> searchPromo(int promo)
    {
        return e -> e.getPromo() == promo;
    }

    private Predicate<Student> searchCity(String city)
    {
        return e -> e.getCity().getTitle().toLowerCase().equalsIgnoreCase(city);
    }

    private String returnPattern(String in, String index)
    {
        Pattern pattern = Pattern.compile(index);
        Matcher matcher = pattern.matcher(in);

        if (matcher.find())
            return in.substring(matcher.start());
        return null;
    }

    private Predicate<Student> searchStudent(String[] tab) {
        List<Document> documents = this.studentIndexer.searchIndex("body", tab[1].toLowerCase() + (tab.length == 3 ? tab[2].toLowerCase() : ""), this.maxStudents);

        List<Student> std = documents.stream().map(e -> this.students.stream().filter(a ->
                a.getTitle().equals(e.get("body"))).findFirst().orElse(null)
        ).collect(Collectors.toList());

        return e -> std.stream().filter(a -> a.getTitle().equalsIgnoreCase(e.getTitle())).findFirst().orElse(null) != null;
    }

    private double StringTODouble(String gpa)
    {
        try {
            return Double.valueOf(gpa);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public List<Student> searchAll(String searchQuery)
    {
        String al;

        if ((al = this.returnPattern(searchQuery, "gpa")) != null) {
            String tab[] = al.split( " ");

            this.students = this.students.stream().filter(this.searchGPA(Double.valueOf(tab[2]), tab[1])).collect(Collectors.toList());
        }

        if ((al = this.returnPattern(searchQuery, "promo")) != null) {
            String tab[] = al.split( " ");

            this.students = this.students.stream().filter(this.searchPromo(Integer.valueOf(tab[1]))).collect(Collectors.toList());
        }

        if ((al = this.returnPattern(searchQuery, "student")) != null) {
            String tab[] = al.split( " ");

            this.students = this.students.stream().filter(this.searchStudent(tab)).collect(Collectors.toList());
        }

        if ((al = this.returnPattern(searchQuery, "city")) != null) {
            String tab[] = al.split( " ");

            this.students = this.students.stream().filter(this.searchCity(tab[1].toLowerCase())).collect(Collectors.toList());
        }

        return this.students.stream().sorted((a, b) ->
                Double.compare(StringTODouble(b.getGpa().get(0).getGpa()), StringTODouble(a.getGpa().get(0).getGpa()))
        ).collect(Collectors.toList());
    }
}
