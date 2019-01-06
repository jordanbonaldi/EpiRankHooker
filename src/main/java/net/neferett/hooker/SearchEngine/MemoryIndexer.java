package net.neferett.hooker.SearchEngine;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.util.ArrayList;
import java.util.List;

@Data
public class MemoryIndexer {

    private Directory memoryIndex;
    private Analyzer analyzer;

    public MemoryIndexer()
    {
        this.memoryIndex = new RAMDirectory();
        this.analyzer = new StandardAnalyzer();
    }

    @SneakyThrows
    public void indexDocument(String title, String body)
    {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
        IndexWriter writter = new IndexWriter(memoryIndex, indexWriterConfig);
        Document document = new Document();

        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new TextField("body", body, Field.Store.YES));
        document.add(new SortedDocValuesField("title", new BytesRef(title)));

        writter.addDocument(document);
        writter.close();
    }

    @SneakyThrows
    public List<Document> searchIndex(String inField, String queryString, int amount)
    {
        Query query = new TermQuery(new Term(inField, queryString));

        IndexReader indexReader = DirectoryReader.open(memoryIndex);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, amount);
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }

        return documents;
    }

    @SneakyThrows
    public void deleteDocument(Term term)
    {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter writter = new IndexWriter(memoryIndex, indexWriterConfig);
        writter.deleteDocuments(term);
        writter.close();

    }

    @SneakyThrows
    public List<Document> searchIndex(Query query, int amount)
    {
        IndexReader indexReader = DirectoryReader.open(memoryIndex);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, amount);
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }

        return documents;
    }

    @SneakyThrows
    public List<Document> searchIndex(Query query, Sort sort, int amount)
    {
        IndexReader indexReader = DirectoryReader.open(memoryIndex);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query,  amount, sort);
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }

        return documents;
    }
}