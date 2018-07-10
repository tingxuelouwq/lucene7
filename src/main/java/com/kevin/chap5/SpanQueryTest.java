package com.kevin.chap5;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * @类名: SpanQueryTest
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/9 11:11
 * @版本：1.0
 * @描述：
 */
public class SpanQueryTest extends TestCase {

    private Directory directory;
    private Analyzer analyzer;
    private IndexReader reader;
    private IndexSearcher searcher;

    private SpanTermQuery quick;
    private SpanTermQuery brown;
    private SpanTermQuery red;
    private SpanTermQuery fox;
    private SpanTermQuery lazy;
    private SpanTermQuery sleepy;
    private SpanTermQuery dog;
    private SpanTermQuery cat;

    @Override
    public void setUp() throws IOException {
        directory = new RAMDirectory();
        analyzer = new WhitespaceAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, config);
        Document doc = new Document();
        doc.add(new TextField("f",
                "the quick brown fox jumps over the lazy dog",
                Field.Store.YES));
        writer.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("f",
                "the quick red fox jumps over the sleepy cat",
                Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);

        quick = new SpanTermQuery(new Term("f", "quick"));
        brown = new SpanTermQuery(new Term("f", "brown"));
        red = new SpanTermQuery(new Term("f", "red"));
        fox = new SpanTermQuery(new Term("f", "fox"));
        lazy = new SpanTermQuery(new Term("f", "lazy"));
        sleepy = new SpanTermQuery(new Term("f", "sleepy"));
        dog = new SpanTermQuery(new Term("f", "dog"));
        cat = new SpanTermQuery(new Term("f", "cat"));
    }

    private void assertOnlyBrownFox(Query query) throws IOException {
        TopDocs hits = searcher.search(query, 10);
        assertEquals(1, hits.totalHits);
        assertEquals("wrong doc", 0, hits.scoreDocs[0].doc);
    }

    private void assertOnlyRedFox(Query query) throws IOException {
        TopDocs hits = searcher.search(query, 10);
        assertEquals(1, hits.totalHits);
        assertEquals("wrong doc", 1, hits.scoreDocs[0].doc);
    }

    private void assertBothFoxes(Query query) throws IOException {
        TopDocs hits = searcher.search(query, 10);
        assertEquals(2, hits.totalHits);
    }

    private void assertNoMatches(Query query) throws IOException {
        TopDocs hits = searcher.search(query, 10);
        assertEquals(0, hits.totalHits);
    }

    public void testSpanFirstQuery() throws IOException {
        SpanFirstQuery spanFirstQuery = new SpanFirstQuery(brown, 2);
        System.out.println(spanFirstQuery); // spanFirst(f:brown, 2)
        assertNoMatches(spanFirstQuery);

        spanFirstQuery = new SpanFirstQuery(brown, 3);
        System.out.println(spanFirstQuery); // spanFirst(f:brown, 3)
        assertOnlyBrownFox(spanFirstQuery);
    }

    public void testSpanNearQuery() throws IOException {
        SpanQuery[] spanQueries = new SpanQuery[] {quick, brown, dog};
        SpanNearQuery spanNearQuery = new SpanNearQuery(spanQueries, 0, true);
        System.out.println(spanNearQuery);  // spanNear([f:quick, f:brown, f:dog], 0, true)
        assertNoMatches(spanNearQuery);

        spanNearQuery = new SpanNearQuery(spanQueries, 4, true);
        System.out.println(spanNearQuery);  // spanNear([f:quick, f:brown, f:dog], 4, true)
        assertNoMatches(spanNearQuery);

        spanNearQuery = new SpanNearQuery(spanQueries, 5, true);
        System.out.println(spanNearQuery);  // spanNear([f:quick, f:brown, f:dog], 5, true)
        assertOnlyBrownFox(spanNearQuery);

        spanQueries = new SpanQuery[] {lazy, fox};
        spanNearQuery = new SpanNearQuery(spanQueries, 3, false);
        System.out.println(spanNearQuery);  // spanNear([f:lazy, f:fox], 3, false)
        assertOnlyBrownFox(spanNearQuery);

        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.setSlop(4);
        builder.add(new Term("f", "lazy"));
        builder.add(new Term("f", "fox"));
        PhraseQuery phraseQuery = builder.build();
        System.out.println(phraseQuery);    // f:"lazy fox"~4
        assertNoMatches(phraseQuery);

        builder.setSlop(5);
        phraseQuery = builder.build();
        System.out.println(phraseQuery);    // f:"lazy fox"~5
        assertOnlyBrownFox(phraseQuery);
    }

    public void testSpanNotQuery() throws IOException {
        SpanQuery[] spanQueries = new SpanQuery[]{quick, fox};
        SpanNearQuery spanNearQuery = new SpanNearQuery(spanQueries, 1, true);
        System.out.println(spanNearQuery);  // spanNear([f:quick, f:fox], 1, true)
        assertBothFoxes(spanNearQuery);

        SpanNotQuery spanNotQuery = new SpanNotQuery(spanNearQuery, dog);
        System.out.println(spanNotQuery);   // spanNot(spanNear([f:quick, f:fox], 1, true), f:dog, 0, 0)
        assertBothFoxes(spanNotQuery);

        spanNotQuery = new SpanNotQuery(spanNearQuery, red);
        System.out.println(spanNotQuery);   // spanNot(spanNear([f:quick, f:fox], 1, true), f:red, 0, 0)
        assertOnlyBrownFox(spanNotQuery);

        spanNotQuery = new SpanNotQuery(spanNearQuery, dog, 4, 5);
        System.out.println(spanNotQuery);   // spanNot(spanNear([f:quick, f:fox], 1, true), f:dog, 4, 4)
        TopDocs hits = searcher.search(spanNotQuery, 10);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            // Document<stored,indexed,tokenized<f:the quick red fox jumps over the sleepy cat>>
            System.out.println(searcher.doc(scoreDoc.doc));
        }
    }

    public void testSpanOrQuery() throws IOException {
        SpanNearQuery quickFox = new SpanNearQuery(new SpanQuery[]{quick, fox}, 1, true);
        SpanNearQuery lazyDog = new SpanNearQuery(new SpanQuery[]{lazy, dog}, 0, true);
        SpanNearQuery sleepyCat = new SpanNearQuery(new SpanQuery[]{sleepy, cat}, 0, true);

        SpanNearQuery qfNearLd = new SpanNearQuery(new SpanQuery[]{quickFox, lazyDog}, 3, true);
        assertOnlyBrownFox(qfNearLd);
        SpanNearQuery qfNearSc = new SpanNearQuery(new SpanQuery[]{quickFox, sleepyCat}, 3, true);
        assertOnlyRedFox(qfNearSc);

        SpanOrQuery or = new SpanOrQuery(new SpanQuery[]{qfNearLd, qfNearSc});
        assertBothFoxes(or);
    }
}
