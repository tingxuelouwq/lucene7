package com.kevin.chap8.grouping;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 类名: GroupingUtil<br/>
 * 包名：com.kevin.chap8.grouping<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/22 11:04<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class GroupingUtil {

    public static Directory createIndex(String indexDir, String groupField) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(indexDir));
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, config);
        addDocuments(groupField, writer);
        return directory;
    }

    private static void addDocuments(String groupField, IndexWriter writer) throws IOException {
        // 0
        Document doc = new Document();
        addGroupField(doc, groupField, "author1");
        doc.add(new TextField("content", "random text", Field.Store.YES));
        doc.add(new StringField("id", "1", Field.Store.YES));
        writer.addDocument(doc);
        // 1
        doc = new Document();
        addGroupField(doc, groupField, "author1");
        doc.add(new TextField("content", "some more random text", Field.Store.YES));
        doc.add(new StringField("id", "2", Field.Store.YES));
        writer.addDocument(doc);
        // 2
        doc = new Document();
        addGroupField(doc, groupField, "author1");
        doc.add(new TextField("content", "some more random textual data", Field.Store.YES));
        doc.add(new StringField("id", "3", Field.Store.YES));
        writer.addDocument(doc);
        // 3
        doc = new Document();
        addGroupField(doc, groupField, "author2");
        doc.add(new TextField("content", "some random text", Field.Store.YES));
        doc.add(new StringField("id", "4", Field.Store.YES));
        writer.addDocument(doc);
        // 4
        doc = new Document();
        addGroupField(doc, groupField, "author3");
        doc.add(new TextField("content", "some more random text", Field.Store.YES));
        doc.add(new StringField("id", "5", Field.Store.YES));
        writer.addDocument(doc);
        // 5
        doc = new Document();
        addGroupField(doc, groupField, "author3");
        doc.add(new TextField("content", "random", Field.Store.YES));
        doc.add(new StringField("id", "6", Field.Store.YES));
        writer.addDocument(doc);
        // 6
        doc = new Document();
        doc.add(new TextField("content", "random word stuck in alot of other text", Field.Store.YES));
        doc.add(new StringField("id", "7", Field.Store.YES));
        writer.addDocument(doc);

        writer.close();
    }

    private static void addGroupField(Document doc, String groupField, String value) {
        doc.add(new StringField(groupField, value, Field.Store.YES));
        doc.add(new SortedDocValuesField(groupField, new BytesRef(value)));
    }
}
