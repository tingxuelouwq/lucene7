package com.kevin.chap3;

import com.kevin.util.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.junit.Test;

import java.io.IOException;

/**
 * @类名: BasicSearchingTest
 * @包名：com.kevin.chap3
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/25 10:34
 * @版本：1.0
 * @描述：
 */
public class BasicSearchingTest extends TestCase {

    @Test
    public void testTerm() throws IOException {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Term term = new Term("contents", "apache");
        Query query = new TermQuery(term);
        TopDocs docs = searcher.search(query, 10);
        assertEquals("Apache", 3, docs.totalHits);
        reader.close();
        dir.close();
    }

    @Test
    public void testQueryParser() throws IOException, ParseException {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query query = parser.parse("apache");
        TopDocs docs = searcher.search(query, 10);
        assertEquals("Apache", 3, docs.totalHits);
        reader.close();
        dir.close();
    }
}
