package com.kevin.chap1;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

/**
 * @类名: IndexerForLuceneAction
 * @包名：com.kevin.chap1
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/4 10:16
 * @版本：1.0
 * @描述：
 */
public class IndexerForLuceneAction {
    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = "D:\\Lucene\\Index";
        String dataDir = "D:\\Lucene\\TEST";
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig());
        File[] files = new File(dataDir).listFiles();
        for (File file : files) {
            Document doc = getDocument(file);
            writer.addDocument(doc);
        }
        writer.close();
        dir.close();
    }

    private static Document getDocument(File file) throws IOException, ParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        Document doc = new Document();
        String fullpath = file.getCanonicalPath();
        String isbn = props.getProperty("isbn");
        String title = props.getProperty("title");
        String author = props.getProperty("author");
        String url = props.getProperty("url");
        String subject = props.getProperty("subject");
        String pubmonth = props.getProperty("pubmonth");
        System.out.println(title + "\n" + author + "\n" + subject + "\n" + pubmonth + "\n" + fullpath + "\n---------");
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        doc.add(new StringField("category", fullpath, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("title2", title.toLowerCase(), Field.Store.YES));
        doc.add(new StringField("url", url, Field.Store.YES));
        doc.add(new TextField("subject", subject, Field.Store.YES));
        doc.add(new IntPoint("pubmonth", Integer.valueOf(pubmonth)));
        Date date = DateTools.stringToDate(pubmonth);
        doc.add(new IntPoint("pubmonthAsDay", (int) (date.getTime() / (1000 * 3600 *24))));
        String[] authors = author.split(",");
        for (String a : authors) {
            doc.add(new StringField("author", a, Field.Store.YES));
        }
        for (String text : new String[] {title, subject, author, fullpath}) {
            doc.add(new TextField("contents", text, Field.Store.NO));
        }
        return doc;
    }
}
