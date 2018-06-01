package com.kevin.chap3;

import junit.framework.TestCase;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * @类名: NearRealTimeTest
 * @包名：com.kevin.chap3
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/1 16:34
 * @版本：1.0
 * @描述：
 */
public class NearRealTimeTest extends TestCase {

    public void testNearRealTime() throws Exception {
        Directory dir = new RAMDirectory();
        IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig());
        for (int i = 0; i < 10; i++) {
            Document doc = new Document();
            doc.add(new StringField("id", "" + i, Field.Store.NO));
            doc.add(new TextField("text", "aaa", Field.Store.NO));
            writer.addDocument(doc);
        }

        // 创建近实时Reader
        IndexReader reader = DirectoryReader.open(writer);
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = new TermQuery(new Term("text", "aaa"));
        TopDocs topDocs = searcher.search(query, 1);
        System.out.println(topDocs.totalHits);  // 10

        // 删除一个文档
        writer.deleteDocuments(new Term("id", "7"));

        // 添加一个文档
        Document doc = new Document();
        doc.add(new StringField("id", "11", Field.Store.NO));
        doc.add(new TextField("text", "bbb", Field.Store.NO));
        writer.addDocument(doc);

        // 重启Reader
        IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader) reader);
        // 确认Reader是新建的
        System.out.println(reader == newReader);    // false
        // 关闭旧的Reader
        reader.close();
        searcher = new IndexSearcher(newReader);
        TopDocs hits = searcher.search(query, 10);
        System.out.println(hits.totalHits); // 9

        query = new TermQuery(new Term("text", "bbb"));
        hits = searcher.search(query, 1);
        System.out.println(hits.totalHits); // 1

        newReader.close();
        writer.close();
    }
}
