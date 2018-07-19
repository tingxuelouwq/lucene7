package com.kevin.chap6;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * @类名: DistanceSortingTest
 * @包名：com.kevin.chap6
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/19 10:22
 * @版本：1.0
 * @描述：
 */
public class DistanceSortingTest extends TestCase {

    private Directory directory;
    private IndexReader reader;
    private IndexSearcher searcher;
    private Query query;

    @Override
    public void setUp() throws IOException {
        directory = new RAMDirectory();
        Analyzer analyzer = new WhitespaceAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, config);
        addPoint(writer, "El Charro", "restaurant", 1, 2);
        addPoint(writer, "Cafe Poca Cosa", "restaurant", 5, 9);
        addPoint(writer, "Los Betos", "restaurant", 9, 6);
        addPoint(writer, "Nico's Taco Shop", "restaurant", 3, 8);
        writer.close();

        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
        query = new TermQuery(new Term("type", "restaurant"));
    }

    private void addPoint(IndexWriter writer, String name, String type, int x, int y)
            throws IOException {
        Document document = new Document();
        document.add(new StringField("name", name, Field.Store.YES));
        document.add(new StringField("type", type, Field.Store.YES));
        document.add(new StringField("location", x + "," + y, Field.Store.YES));
        writer.addDocument(document);
    }

    public void testNearestRestaurantToHome() throws IOException {
        Sort sort = new Sort(new SortField("location",
                new DistanceComparatorSource(0, 0)));
        TopDocs hits = searcher.search(query, 10, sort);
        ScoreDoc[] scoreDocs = hits.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("name"));
        }
    }
}
