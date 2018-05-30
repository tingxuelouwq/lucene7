package com.kevin.chap2;

import com.kevin.util.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import java.io.IOException;

/**
 * @类名: IndexingTest
 * @包名：com.kevin.chap2
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/25 9:07
 * @版本：1.0
 * @描述：
 */
public class IndexingTest extends TestCase {
    private String[] ids = {"1", "2"};
    private String[] countries = {"Netherlands", "Italy"};
    private String[] contents = {"Amsterdam has lots of bridges",
            "Venice has lots of canals"};
    private String[] cities = {"Amsterdam", "Venice"};
    private Directory directory;

    @Override
    protected void setUp() throws Exception {
        directory = new RAMDirectory();
        IndexWriter writer = getWriter();
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("country", countries[i], Field.Store.YES));
            doc.add(new TextField("contents", contents[i], Field.Store.NO));
            doc.add(new TextField("city", cities[i], Field.Store.YES));
            writer.addDocument(doc);
        }
        writer.close();
    }

    private IndexWriter getWriter() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig();
        return new IndexWriter(directory, config);
    }

    private long getHitCount(String fieldName, String searchString) throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Term term = new Term(fieldName, searchString);
        Query query = new TermQuery(term);
        long hitCount = TestUtil.hitCount(searcher, query);
        reader.close();
        return hitCount;
    }

    @Test
    public void testIndexWriter() throws IOException {
        IndexWriter writer = getWriter();
        assertEquals(ids.length, writer.numDocs());
        writer.close();
    }

    @Test
    public void testIndexReader() throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        assertEquals(ids.length, reader.maxDoc());
        assertEquals(ids.length, reader.numDocs());
        reader.close();
    }

    @Test
    public void testGetHitCount() throws IOException {
        String fieldName = "contents";
        String searchString = "amsterdam";
        System.out.println(getHitCount(fieldName, searchString));
    }

    @Test
    public void testDeleteBeforeOptimize() throws IOException {
        IndexWriter writer = getWriter();
        assertEquals(2, writer.numDocs());
        writer.deleteDocuments(new Term("id", "1"));
        writer.commit();
        System.out.println(writer.hasDeletions());  // true
        System.out.println(writer.maxDoc());    // 2
        System.out.println(writer.numDocs());   // 1
        writer.close();
    }

    @Test
    public void testDeleteAfterOptimize() throws IOException {
        IndexWriter writer = getWriter();
        assertEquals(2, writer.numDocs());
        writer.deleteDocuments(new Term("id", "1"));
        writer.forceMergeDeletes();
        writer.commit();
        System.out.println(writer.hasDeletions());  // false
        System.out.println(writer.maxDoc());    // 1
        System.out.println(writer.numDocs());   // 1
        writer.close();
    }

    @Test
    public void testUpdate() throws IOException {
        System.out.println(getHitCount("city", "amsterdam"));
        IndexWriter writer = getWriter();
        Document doc = new Document();
        doc.add(new StringField("id", "1", Field.Store.YES));
        doc.add(new StringField("country", "Netherlands", Field.Store.YES));
        doc.add(new TextField("contents", "China has a lot of meseums", Field.Store.NO));
        doc.add(new TextField("city", "China", Field.Store.YES));
        writer.updateDocument(new Term("id", "1"), doc);
        writer.close();
        System.out.println(getHitCount("city", "amsterdam"));
        System.out.println(getHitCount("city", "china"));
    }
}
