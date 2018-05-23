package com.kevin.chap1;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;

/**
 * @类名: Indexer
 * @包名：com.kevin.chap1
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/23 14:43
 * @版本：1.0
 * @描述：索引.txt文件
 */
public class Indexer {

    private IndexWriter writer;

    public static void main(String[] args) throws IOException {
        String indexDir = "D:\\Lucene\\Index";  // 索引存放目录
        String dataDir = "D:\\Lucene\\Data";    // 原始内容目录

        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numIndexed;
        try {
            numIndexed = indexer.index(dataDir, new TextFilesFilter());
        } finally {
            indexer.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("Indexing " + numIndexed + " files took " +
                (end - start) + " ms");
    }

    public Indexer(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        writer = new IndexWriter(dir, config);
    }

    /**
     * 关闭IndexWriter
     * @throws IOException
     */
    public void close() throws IOException {
        writer.close();
    }

    public int index(String dataDir, FileFilter fileFilter) throws IOException {
        File[] files = new File(dataDir).listFiles();
        for (File file : files) {
            if (!file.isDirectory() &&
                    !file.isHidden() &&
                    file.exists() &&
                    file.canRead() &&
                    (fileFilter == null || fileFilter.accept(file))) {
                indexFile(file);
            }
        }
        return writer.numDocs();
    }

    private void indexFile(File file) throws IOException {
        System.out.println("Indexing " + file.getCanonicalPath());
        Document doc = getDocument(file);
        writer.addDocument(doc);
    }

    private Document getDocument(File file) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("contents", new FileReader(file)));
        doc.add(new StringField("filename", file.getName(), Field.Store.YES));
        doc.add(new StringField("fullpath", file.getCanonicalPath(), Field.Store.YES));
        return doc;
    }

    private static class TextFilesFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase()
                    .endsWith(".txt");
        }
    }
}
