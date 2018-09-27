package com.kevin.chap10.multisearch;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 类名: MultiThreadSearchTest<br/>
 * 包名：com.kevin.chap10.multisearch<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/27 14:20<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class MultiThreadSearchTest {

    public static void main(String[] args) throws IOException {
        // 多索引目录查询（把多个索引目录当做一个索引目录）
        multiReaderSearch();
    }

    /**
     * 多索引目录查询（把多个索引目录当做一个索引目录）
     * @throws IOException
     */
    public static void multiReaderSearch() throws IOException {
        Directory dir1 = FSDirectory.open(Paths.get("D:/index1"));
        Directory dir2 = FSDirectory.open(Paths.get("D:/index2"));
        IndexReader reader1 = DirectoryReader.open(dir1);
        IndexReader reader2 = DirectoryReader.open(dir2);
        MultiReader multiReader = new MultiReader(reader1, reader2);
        IndexSearcher indexSearcher = new IndexSearcher(multiReader);
        Query query = new TermQuery(new Term("contents", "人民"));
        TopDocs hits = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = hits.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document doc = indexSearcher.doc(docId);
            System.out.println(doc.get("path"));
            System.out.println(doc.get("contents"));
        }
    }

    /**
     * 多线程查询
     * @throws IOException
     */
    public static void multiThreadSearch() throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Directory dir1 = FSDirectory.open(Paths.get("D:/index1"));
        Directory dir2 = FSDirectory.open(Paths.get("D:/index2"));
        IndexReader reader1 = DirectoryReader.open(dir1);
        IndexReader reader2 = DirectoryReader.open(dir2);
        MultiReader multiReader = new MultiReader(reader1, reader2);

    }
}
