package com.kevin.chap5;

import com.kevin.chap4.synonymanalyzer.SynonymAnalyzer;
import com.kevin.chap4.synonymanalyzer.SynonymEngine;
import junit.framework.TestCase;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;
import sun.text.normalizer.NormalizerBase;

import java.io.IOException;

/**
 * @类名: MultiPhraseQueryTest
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/29 14:40
 * @版本：1.0
 * @描述：
 */
public class MultiPhraseQueryTest extends TestCase {

    private Directory directory;
    private IndexReader reader;
    private IndexSearcher searcher;

    @Override
    public void setUp() throws Exception {
        directory = new RAMDirectory();
        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig());
        Document doc1 = new Document();
        doc1.add(new TextField("field",
                "the quick brown fox jumped over the lazy dog",
                Field.Store.YES));
        writer.addDocument(doc1);
        Document doc2 = new Document();
        doc2.add(new TextField("field",
                        "the fast fox hopped over the hound",
                Field.Store.YES));
        writer.addDocument(doc2);
        writer.close();

        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    @Override
    public void tearDown() throws IOException {
        reader.close();
        directory.close();
    }

    @Test
    public void testBasic() throws IOException {
        MultiPhraseQuery.Builder builder = new MultiPhraseQuery.Builder();
        builder.add(new Term[]{
                new Term("field", "quick"),
                new Term("field", "fast")
        });
        builder.add(new Term("field", "fox"));
        MultiPhraseQuery query = builder.build();
        System.out.println(query);
        TopDocs topDocs = searcher.search(query, 10);
        System.out.println(topDocs.totalHits);  // 1

        builder.setSlop(1);
        query = builder.build();
        System.out.println(query);
        topDocs = searcher.search(query, 10);
        System.out.println(topDocs.totalHits);  // 2
    }

    @Test
    public void testAgainstOR() throws IOException {
        PhraseQuery.Builder quickFoxBuilder = new PhraseQuery.Builder();
        quickFoxBuilder.setSlop(1);
        quickFoxBuilder.add(new Term("field", "quick"));
        quickFoxBuilder.add(new Term("field", "fox"));
        PhraseQuery quickFox = quickFoxBuilder.build();

        PhraseQuery.Builder fastFoxBuilder = new PhraseQuery.Builder();
        fastFoxBuilder.add(new Term("field", "fast"));
        fastFoxBuilder.add(new Term("field", "fox"));
        PhraseQuery fastFox = fastFoxBuilder.build();

        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queryBuilder.add(quickFox, BooleanClause.Occur.SHOULD);
        queryBuilder.add(fastFox, BooleanClause.Occur.SHOULD);
        BooleanQuery query = queryBuilder.build();

        TopDocs hits = searcher.search(query, 10);
        System.out.println(hits.totalHits); // 2
    }

    @Test
    public void testQueryParser() throws ParseException {
        SynonymEngine engine = new SynonymEngine() {
            @Override
            public String[] getSynonyms(String s) throws IOException {
                if (s.equals("quick")) {
                    return new String[]{"fast"};
                } else {
                    return null;
                }
            }
        };

        QueryParser parser = new QueryParser("field", new SynonymAnalyzer(engine));
        Query query = parser.parse("\"quick fox\"");
        System.out.println(query.toString());   // field:"(quick fast) fox"
        System.out.println(query.getClass().getName()); // org.apache.lucene.search.MultiPhraseQuery
    }
}
