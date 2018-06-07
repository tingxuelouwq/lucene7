package com.kevin.chap3;

import junit.framework.TestCase;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import java.io.IOException;

/**
 * @类名: WildcardQueryTest
 * @包名：com.kevin.chap3
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/6 11:16
 * @版本：1.0
 * @描述：
 */
public class WildcardQueryTest extends TestCase {

    private Directory dir;
    private IndexReader reader;
    private IndexSearcher searcher;

    private void indexSingleFieldDocs(Field[] fields) throws IOException {
        dir = new RAMDirectory();
        IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig());
        for (Field field : fields) {
            Document doc = new Document();
            doc.add(field);
            writer.addDocument(doc);
        }
        writer.close();
        reader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(reader);
    }

    @Override
    public void tearDown() throws IOException {
        reader.close();
        dir.close();
    }

    @Test
    public void testWildcardQuery() throws IOException {
        indexSingleFieldDocs(new Field[]{
                new TextField("contents", "wild", Field.Store.YES),
                new TextField("contents", "child", Field.Store.YES),
                new TextField("contents", "mild", Field.Store.YES),
                new TextField("contents", "mildew", Field.Store.YES)
        });
        Query query = new WildcardQuery(new Term("contents", "?ild*"));
        TopDocs matches = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : matches.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("contents"));
        }
    }

    @Test
    public void testFuzzyQuery() throws IOException {
        indexSingleFieldDocs(new Field[]{
                new TextField("contents", "fuzzy", Field.Store.YES),
                new TextField("contents", "wuzzy", Field.Store.YES)
        });
        Query query = new FuzzyQuery(new Term("contents", "wuzzy"));
        TopDocs matches = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : matches.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("contents"));
        }
    }

    @Test
    public void testMatchAllDocsQuery() throws IOException {
        indexSingleFieldDocs(new Field[]{
                new TextField("contents", "hello", Field.Store.YES),
                new TextField("contents", "world", Field.Store.YES)
        });
        Query query = new MatchAllDocsQuery();
        TopDocs topDocs = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("contents"));
        }
    }
}