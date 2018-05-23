package com.kevin.chap1;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @类名: Searcher
 * @包名：com.kevin.chap1
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/23 15:22
 * @版本：1.0
 * @描述：搜索索引文件
 */
public class Searcher {

    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = "D:\\Lucene\\Index";  // 索引存放目录
        String q = "Apache";
        search(indexDir, q);
    }

    public static void search(String indexDir, String q) throws IOException, ParseException {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query query = parser.parse(q);
        long start = System.currentTimeMillis();
        TopDocs hits = searcher.search(query, 10);
        long end = System.currentTimeMillis();
        System.out.println("Found " + hits.totalHits +
            " document(s) in " + (end - start) + " ms" +
            " that matched query '" + q + "'");
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(doc.get("fullpath"));
        }

        reader.close();
    }
}
