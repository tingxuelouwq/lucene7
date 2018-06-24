package com.kevin.chap1;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        String dataDir = "D:\\Lucene\\data";
        String indexDir = "D:\\Lucene\\index";
        List<File> results = new ArrayList<>();
        findFiles(results, new File(dataDir));
        System.out.println(results.size() + " books to index");
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig());
        for (File file : results) {
            Document doc = getDocument(dataDir, file);
            writer.addDocument(doc);
        }
        writer.close();
        dir.close();
    }

    private static void findFiles(List<File> result, File dir) {
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".properties")) {
                result.add(file);
            } else if (file.isDirectory()) {
                findFiles(result, file);
            }
        }
    }

    private static Document getDocument(String rootDir, File file) throws IOException, ParseException {
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        Document doc = new Document();
        String category = file.getParent().substring(rootDir.length()).replace(File.separatorChar, '/');
        String isbn = props.getProperty("isbn");
        String title = props.getProperty("title");
        String author = props.getProperty("author");
        String url = props.getProperty("url");
        String subject = props.getProperty("subject");
        String pubmonth = props.getProperty("pubmonth");
        System.out.println(title + "\n" + author + "\n" + subject + "\n" + pubmonth + "\n" + category + "\n---------");
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        doc.add(new StringField("category", category, Field.Store.YES));
        doc.add(new SortedDocValuesField("category", new BytesRef(category)));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("title2", title.toLowerCase(), Field.Store.YES));
        doc.add(new StringField("url", url, Field.Store.YES));
        doc.add(new TextField("subject", subject, Field.Store.YES));
        Integer intPubmonth = Integer.valueOf(pubmonth);
        doc.add(new IntPoint("pubmonth", intPubmonth));
        doc.add(new StoredField("pubmonth", intPubmonth));
        doc.add(new NumericDocValuesField("pubmonth", intPubmonth));
        Date date = DateTools.stringToDate(pubmonth);
        doc.add(new IntPoint("pubmonthAsDay", (int) (date.getTime() / (1000 * 3600 *24))));
        String[] authors = author.split(",");
        for (String a : authors) {
            doc.add(new StringField("author", a, Field.Store.YES));
        }
        for (String text : new String[] {title, subject, author, category}) {
            doc.add(new TextField("contents", text, Field.Store.NO));
        }
        return doc;
    }
}
