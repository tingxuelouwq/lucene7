package com.kevin.chap5;

import com.kevin.util.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

/**
 * @类名: FilterTest
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/11 9:08
 * @版本：1.0
 * @描述：
 */
public class FilterTest extends TestCase {

    private Directory dir;
    private IndexReader reader;
    private IndexSearcher searcher;

    @Override
    public void setUp() throws IOException {
        dir = TestUtil.getBookIndexDirectory();
        reader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(reader);
    }

    @Override
    public void tearDown() throws IOException {
        reader.close();
        dir.close();
    }

    public void testTermRangeFilter() throws IOException {
        TermRangeQuery termRangeQuery = new TermRangeQuery("title2",
                new BytesRef("d"), new BytesRef("j"),
                true, true);
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(termRangeQuery, BooleanClause.Occur.FILTER);
        Query query = builder.build();
        TopDocs hits = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("title2"));
        }
    }

    public void testPointRangeFiler() throws IOException {
        Query pointRangeQuery = IntPoint.newRangeQuery("pubmonth",
                201001, 201006);
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(pointRangeQuery, BooleanClause.Occur.FILTER);
        Query query = builder.build();
        TopDocs hits = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("title2"));
        }
    }

    public void testPrefixFilter() throws IOException {
        PrefixQuery prefixQuery = new PrefixQuery(new Term(
                "category", "/technology/computers"));
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(prefixQuery, BooleanClause.Occur.FILTER);
        Query query = builder.build();
        TopDocs hits = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("title2"));
        }
    }
}
