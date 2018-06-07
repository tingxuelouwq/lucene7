package com.kevin.chap3;

import junit.framework.TestCase;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
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
    public void testTermQuery() throws IOException {
        Term term = new Term("isbn", "9781935182023");
        Query query = new TermQuery(term);
        TopDocs topDocs = searcher.search(query, 10);
        printField(topDocs, "category");
    }

    @Test
    public void testTermRangQuery() throws IOException {
        TermRangeQuery query = TermRangeQuery.newStringRange("title2",
                "d", "j", true, true);
        TopDocs topDocs = searcher.search(query, 100);
        printField(topDocs, "category");
    }

    @Test
    public void testNumericRangeQuery() throws IOException {
        Query query = IntPoint.newRangeQuery("pubmonth", 200605, 200609);
        TopDocs topDocs = searcher.search(query, 10);
        printField(topDocs, "category");
    }

    @Test
    public void testPrefixQuery() throws IOException {
        Term term = new Term("category", "/technology/computers/programming");
        PrefixQuery query = new PrefixQuery(term);
        TopDocs topDocs = searcher.search(query, 10);
        printField(topDocs, "category");
        System.out.println("---------");
        TermQuery query2 = new TermQuery(term);
        topDocs = searcher.search(query2, 10);
        printField(topDocs, "category");
    }

    @Test
    public void testAnd() throws IOException {
        TermQuery query1 = new TermQuery(new Term("subject", "java"));
        Query query2 = IntPoint.newRangeQuery("pubmonth", 201001, 201012);
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(query1, BooleanClause.Occur.MUST);
        builder.add(query2, BooleanClause.Occur.SHOULD);
        BooleanQuery query = builder.build();
        TopDocs topDocs = searcher.search(query, 10);
        printField(topDocs, "subject");
    }

    @Test
    public void testOr() throws IOException {
        TermQuery query1 = new TermQuery(new Term("category", "/technology/computers/programming/methodology"));
        TermQuery query2 = new TermQuery(new Term("category", "/philosophy/eastern"));
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(query1, BooleanClause.Occur.SHOULD);
        builder.add(query2, BooleanClause.Occur.SHOULD);
        BooleanQuery query = builder.build();
        TopDocs topDocs = searcher.search(query, 10);
        printField(topDocs, "subject");
    }

    private void printField(TopDocs topDocs, String field) throws IOException {
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get(field));
        }
    }

    @Test
    public void testToString() {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(new FuzzyQuery(new Term("field", "kountry")),
                BooleanClause.Occur.MUST);
        builder.add(new TermQuery(new Term("title", "western")),
                BooleanClause.Occur.SHOULD);
        BooleanQuery query = builder.build();
        System.out.println(query.toString());
    }

    @Test
    public void testParseTermQuery() throws ParseException {
        QueryParser parser = new QueryParser("subject", new StandardAnalyzer());
        Query query = parser.parse("computers");
        System.out.println(query);
    }

    @Test
    public void testParseTermRangeQuery() throws ParseException, IOException {
        QueryParser parser = new QueryParser("subject", new StandardAnalyzer());
        Query query = parser.parse("title2: [Q TO V]");
        System.out.println(query);
        TopDocs topDocs = searcher.search(query, 10);
        printField(topDocs, "title2");

        System.out.println("----------");

        parser = new QueryParser("subject", new StandardAnalyzer());
        query = parser.parse("title2:{Q TO \"Tapestry in Action\"}");
        System.out.println(query);
        topDocs = searcher.search(query, 10);
        printField(topDocs, "title2");
    }

    @Test
    public void testParseWildcardQuery() throws ParseException {
        QueryParser parser = new QueryParser("field", new StandardAnalyzer());
        Query query = parser.parse("PrefixQuery*");
        System.out.println(query);  // field:prefixquery*
    }

    @Test
    public void testParsePhraseQuery() throws ParseException {
        QueryParser parser = new QueryParser("field", new StandardAnalyzer());
        Query query = parser.parse("\"This is Some Phrase*\"");
        System.out.println(query);  // field:"? ? some phrase"

        query = parser.parse("\"Term\"");
        System.out.println(query);  // field:term

        parser.setPhraseSlop(5);
        query = parser.parse("\"exact phrase\"");
        System.out.println(query);  // field:"exact phrase"~5

        query = parser.parse("\"sloppy phrase\"~3");
        System.out.println(query);  // field:"sloppy phrase"~3
    }

    @Test
    public void testParseFuzzyQuery() throws ParseException {
        QueryParser parser = new QueryParser("subject", new StandardAnalyzer());
        Query query = parser.parse("kountry~");
        System.out.println(query);  // subject:kountry~2
    }

    @Test
    public void testParseGrouping() throws ParseException {
        QueryParser parser = new QueryParser("subject", new StandardAnalyzer());
        Query query = parser.parse("(agile OR extreme) AND methodology");
        System.out.println(query);  // +(subject:agile subject:extreme) +subject:methodology
    }
}
