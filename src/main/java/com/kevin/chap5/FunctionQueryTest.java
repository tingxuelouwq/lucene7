package com.kevin.chap5;

import junit.framework.TestCase;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * @类名: FunctionQueryTest
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/11 11:11
 * @版本：1.0
 * @描述：
 */
public class FunctionQueryTest extends TestCase {

    private Directory dir;
    private IndexReader reader;
    private IndexSearcher searcher;

    @Override
    public void setUp() throws IOException {
        dir = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter writer = new IndexWriter(dir, config);
        Document doc1 = new Document();
        doc1.add(new StringField("score", "7", Field.Store.NO));
        doc1.add(new TextField("content", "this hat is green", Field.Store.NO));
        writer.addDocument(doc1);
        Document doc2 = new Document();
        doc2.add(new StringField("score", "42", Field.Store.NO));
        doc2.add(new TextField("content", "this hat is blue", Field.Store.NO));
        writer.addDocument(doc2);
        writer.close();

        reader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(reader);
    }

    @Override
    public void tearDown() throws IOException {
        reader.close();
        dir.close();
    }

    public void testFunctionScoreQuery() {

    }
}
