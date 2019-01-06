package net.neferett.hooker.SearchEngine;

import lombok.Data;
import net.neferett.hooker.Entity.Student;
import net.neferett.redisapi.Utils.SerializationUtils;
import org.apache.lucene.document.Document;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Engine {

    private MemoryIndexer indexer;

    private int max;

    public Engine() {
        this.indexer = new MemoryIndexer();
    }

    public void loadStudents(List<Student> students) {
        this.max = students.size();
        students.forEach(e ->
            this.indexer.indexDocument("student", SerializationUtils.serialize(e).toString())
        );
    }

    public List<Student> searchAll(String searchQuery) {
        List<Document> documents = this.indexer.searchIndex("body", searchQuery, this.max);

        return documents.stream().map(e -> (Student)SerializationUtils.deSerialize(Student.class, e.get("body"))).collect(Collectors.toList());
    }
}
