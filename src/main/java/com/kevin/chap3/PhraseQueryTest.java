package com.kevin.chap3;

import junit.framework.TestCase;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import java.io.IOException;

/**
 * @类名: PhraseQueryTest
 * @包名：com.kevin.chap3
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/5 21:07
 * @版本：1.0
 * @描述：
 */
public class PhraseQueryTest extends TestCase {

    private Directory dir;
    private IndexReader reader;
    private IndexSearcher searcher;

    @Override
    public void setUp() throws IOException {
        dir = new RAMDirectory();
        IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig());
        Document doc = new Document();
        doc.add(new TextField("field", "the quick brown fox jumped over the lazy dog", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        reader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(reader);
    }

    @Override
    public void tearDown() throws IOException {
        reader.close();
        dir.close();
    }

    private boolean matched(String[] phrase, int slop) throws IOException {
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.setSlop(slop);
        for (String word : phrase) {
            builder.add(new Term("field", word));
        }
        PhraseQuery query = builder.build();
        TopDocs topDocs = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("field"));
        }
        return topDocs.totalHits > 0;
    }

    @Test
    public void testSlopComparison() throws IOException {
        System.out.println(matched(new String[] {"quick", "fox"}, 1)); // true
        System.out.println(matched(new String[]{"fox", "quick"}, 3)); // true
        System.out.println(matched(new String[]{"quick", "jumped", "lazy"}, 4));    // true
        System.out.println(matched(new String[]{"lazy", "jumped", "quick"}, 8));    // true
    }
}
