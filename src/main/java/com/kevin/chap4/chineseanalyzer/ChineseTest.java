package com.kevin.chap4.chineseanalyzer;

import junit.framework.TestCase;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @类名: ChineseTest
 * @包名：com.kevin.chap4.chineseanalyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/14 10:56
 * @版本：1.0
 * @描述：
 */
public class ChineseTest extends TestCase {

    public void testChinese() throws IOException {
        String indexDir = "D:\\Lucene\\index";
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = new TermQuery(new Term("contents", "道"));
        TopDocs topDocs = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("title"));
        }
    }
}
