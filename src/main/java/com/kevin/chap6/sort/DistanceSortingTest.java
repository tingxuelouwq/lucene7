package com.kevin.chap6.sort;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

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
        String xy = x + "," + y;
        document.add(new StringField("name", name, Field.Store.YES));
        document.add(new StringField("type", type, Field.Store.YES));
        document.add(new BinaryDocValuesField("location", new BytesRef(xy)));
        document.add(new StoredField("location", xy));
        writer.addDocument(document);
    }

    public void testNearestRestaurantToHome() throws IOException {
        Sort sort = new Sort(new SortField("location",
                new DistanceComparatorSource(0, 0)));
        TopDocs hits = searcher.search(query, 2, sort);
        ScoreDoc[] scoreDocs = hits.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("name"));
        }
    }

    public void testNearestRestaurantToWork() throws IOException {
        Sort sort = new Sort(new SortField("location",
                new DistanceComparatorSource(10, 10)));
        TopFieldDocs hits = searcher.search(query, 3, sort);
        ScoreDoc[] scoreDocs = hits.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            FieldDoc fieldDoc = (FieldDoc) scoreDoc;
            System.out.println(fieldDoc.fields[0]);
            Document document = searcher.doc(fieldDoc.doc);
            System.out.println(document.get("name"));
        }
    }
}
