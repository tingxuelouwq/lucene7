package com.kevin.util;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @类名: TestUtil
 * @包名：com.kevin.util
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/25 9:56
 * @版本：1.0
 * @描述：
 */
public class TestUtil {

    public static long hitCount(IndexSearcher searcher, Query query) throws IOException {
        return searcher.search(query, 1).totalHits;
    }

    public static Directory getBookIndexDirectory() throws IOException {
        return FSDirectory.open(Paths.get("D:\\Lucene\\index"));
    }

    public static void dumpHits(IndexSearcher searcher, TopDocs hits)
            throws IOException {
        if (hits.totalHits == 0) {
            System.out.println("No hits");
        }

        for (ScoreDoc match : hits.scoreDocs) {
            Document doc = searcher.doc(match.doc);
            System.out.println(match.score + ":" + doc.get("title"));
        }
    }
}
