package com.kevin.chap3;

import junit.framework.TestCase;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @类名: QueryTest
 * @包名：com.kevin.chap3
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/4 9:47
 * @版本：1.0
 * @描述：
 */
public class QueryTest extends TestCase {

    private String indexDir;
    private Directory dir;
    private IndexReader reader;
    private IndexSearcher searcher;

    @Override
    public void setUp() throws IOException {
        indexDir = "D:\\Lucene\\Index";
        dir = FSDirectory.open(Paths.get(indexDir));
        reader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(reader);
    }

    @Override
    public void tearDown() throws IOException {
        reader.close();
        dir.close();
    }

    @Test
    public void testKeyword() throws IOException {
        Term term = new Term("isbn", "9781935182023");
        Query query = new TermQuery(term);
        TopDocs topDocs = searcher.search(query, 10);
        printCategory(topDocs);
    }

    @Test
    public void testTermRangQuery() throws IOException {
        TermRangeQuery query = TermRangeQuery.newStringRange("subject",
                "edu", "education", true, true);
        TopDocs topDocs = searcher.search(query, 10);
        printCategory(topDocs);
    }

    private void printCategory(TopDocs topDocs) throws IOException {
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("category"));
        }
    }
}
