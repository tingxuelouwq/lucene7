package com.kevin.chap4.synonymanalyzer;

import junit.framework.TestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.io.StringReader;

/**
 * @类名: SynonymAnalyzerTest
 * @包名：com.kevin.chap4.synonymanalyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/12 15:35
 * @版本：1.0
 * @描述：
 */
public class SynonymAnalyzerTest extends TestCase {

    private IndexReader reader;
    private IndexSearcher searcher;

    private static SynonymAnalyzer synonymAnalyzer =
            new SynonymAnalyzer(new TestSynonymEngine());

    @Override
    public void setUp() throws IOException {
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(synonymAnalyzer);
        IndexWriter writer = new IndexWriter(directory, config);
        Document document = new Document();
        document.add(new TextField("content",
                "The quick brown fox jumps over the lazy dog",
                Field.Store.YES));
        writer.addDocument(document);
        writer.close();

        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    @Override
    public void tearDown() throws IOException {
        reader.close();
    }

    public void testJumps() throws IOException {
        TokenStream tokenStream =
                synonymAnalyzer.tokenStream("contents",
                        new StringReader("jumps"));
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute positionIncrementAttribute = tokenStream.addAttribute(PositionIncrementAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            int position = positionIncrementAttribute.getPositionIncrement();
            String term = charTermAttribute.toString();
            System.out.println(position + ": " + term);
        }
        tokenStream.end();
        tokenStream.close();
    }

    public void testSearchByAPI() throws IOException {
        Query query = new TermQuery(new Term("content", "leaps"));
        TopDocs topDocs = searcher.search(query, 10);
        System.out.println(topDocs.totalHits);

        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term("content", "fox"));
        builder.add(new Term("content", "hops"));
        query = builder.build();
        topDocs = searcher.search(query, 10);
        System.out.println(topDocs.totalHits);
    }

    public void testWithQueryParser() throws IOException, ParseException {
        QueryParser parser = new QueryParser("content", synonymAnalyzer);
        Query query = parser.parse("\"fox jumps\"");
        TopDocs topDocs = searcher.search(query, 10);
        System.out.println(topDocs.totalHits);
        System.out.println("With SynonymAnalyzer, \"fox jumps\" parses to " +
                query.toString("content"));

        parser = new QueryParser("content", new StandardAnalyzer());
        query = parser.parse("\"fox jumps\"");
        topDocs = searcher.search(query, 10);
        System.out.println(topDocs.totalHits);
        System.out.println("With StandardAnalyzer, \"fox jumps\" parses to " +
                query.toString("content"));
    }
}
