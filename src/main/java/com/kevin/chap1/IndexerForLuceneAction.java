package com.kevin.chap1;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

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

    private IndexWriter writer;

    public static void main(String[] args) throws Exception {
        String dataDir = "D:\\Lucene\\data";
        String indexDir = "D:\\Lucene\\index";
        long start = System.currentTimeMillis();
        IndexerForLuceneAction indexer = new IndexerForLuceneAction(indexDir);
        int numIndexed;
        try {
            numIndexed = indexer.index(dataDir);
        } finally {
            indexer.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("Indexing " + numIndexed + " files took "
                + (end - start) + " milliseconds");
    }

    public IndexerForLuceneAction(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        writer = new IndexWriter(dir, config);
    }

    private void close() throws IOException {
        writer.close();
    }

    private int index(String dataDir) throws Exception {
        List<File> files = new ArrayList<>();
        findFiles(files, new File(dataDir));
        for (File file : files) {
            indexFile(dataDir, file);
        }
        return writer.numDocs();
    }

    private void findFiles(List<File> files, File dataDir) {
        for (File file : dataDir.listFiles()) {
            if (file.getName().toLowerCase().endsWith(".properties")) {
                files.add(file);
            } else if (file.isDirectory()) {
                findFiles(files, file);
            }
        }
    }

    private void indexFile(String dataDir, File file) throws Exception {
        Document doc = getDocument(dataDir, file);
        writer.addDocument(doc);
    }

    private Document getDocument(String rootDir, File file) throws Exception {
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
        doc.add(new SortedDocValuesField("title", new BytesRef(title)));
        doc.add(new TextField("title2", title.toLowerCase(), Field.Store.YES));
        doc.add(new SortedDocValuesField("title2", new BytesRef(title)));
        doc.add(new StringField("url", url, Field.Store.YES));
        doc.add(new SortedDocValuesField("url", new BytesRef(url)));
        FieldType fieldType = new FieldType();
        fieldType.setStored(true);
        fieldType.setTokenized(true);
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        fieldType.setStoreTermVectors(true);
        fieldType.setStoreTermVectorPositions(true);
        fieldType.setStoreTermVectorOffsets(true);
        fieldType.freeze();
        Field subjectField = new Field("subject", subject, fieldType);
        doc.add(subjectField);
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
