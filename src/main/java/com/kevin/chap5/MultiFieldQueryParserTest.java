package com.kevin.chap5;

import com.kevin.util.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.builders.DisjunctionMaxQueryBuilder;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * @类名: MultiFieldQueryParserTest
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/4 10:30
 * @版本：1.0
 * @描述：
 */
public class MultiFieldQueryParserTest extends TestCase {

    @Test
    public void testDefaultOperator() throws ParseException, IOException {
        QueryParser parser = new MultiFieldQueryParser(new String[]{"title", "subject"},
                new SimpleAnalyzer());
        Query query = parser.parse("java lucene");
        // (title:java subject:java) (title:lucene subject:lucene)
        System.out.println(query);
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs hits = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            System.out.print(scoreDoc.score + ": ");
            System.out.println(searcher.doc(scoreDoc.doc).get("title"));
        }
    }

    @Test
    public void testSpecifiedOperator() throws ParseException, IOException {
        Map<String, Float> boosts = new HashMap<>();
        boosts.put("title", 5.0f);
        boosts.put("subject", 10.0f);
        QueryParser parser = new MultiFieldQueryParser(new String[]{"title", "subject"},
                new SimpleAnalyzer(), boosts);
        parser.setDefaultOperator(QueryParser.Operator.AND);
        Query query = parser.parse("java lucene");
        // +((title:java)^5.0 (subject:java)^10.0) +((title:lucene)^5.0 (subject:lucene)^10.0)
        System.out.println(query);
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs hits = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            System.out.print(scoreDoc.score + ": ");
            System.out.println(searcher.doc(scoreDoc.doc).get("title"));
        }
    }

    @Test
    public void testParser() throws ParseException, IOException {
        String queryString = "java lucene";
        String[] fields = {"title", "subject"};
        BooleanClause.Occur[] flags = new BooleanClause.Occur[]{
                BooleanClause.Occur.MUST, BooleanClause.Occur.MUST};
        Analyzer analyzer = new SimpleAnalyzer();
        Query query = MultiFieldQueryParser.parse(queryString, fields, flags, analyzer);
        System.out.println(query);
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs hits = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            System.out.print(scoreDoc.score + ": ");
            System.out.println(searcher.doc(scoreDoc.doc).get("title"));
        }
    }

    @Test
    public void testDisjunctionMaxQuery() throws ParseException, IOException {
        QueryParser parser = new QueryParser("title", new SimpleAnalyzer());
        Query query1 = parser.parse("java lucene");
        parser = new QueryParser("subject", new SimpleAnalyzer());
        Query query2 = parser.parse("java lucene");
        DisjunctionMaxQuery query = new DisjunctionMaxQuery(
                Arrays.asList(query1, query2), 0.7f);
        System.out.println(query);
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs hits = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            System.out.print(scoreDoc.score + ": ");
            System.out.println(searcher.doc(scoreDoc.doc).get("title"));
        }
    }
}
