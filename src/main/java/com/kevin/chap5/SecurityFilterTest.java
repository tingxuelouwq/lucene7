package com.kevin.chap5;

import com.kevin.util.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * @类名: SecurityFilterTest
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/11 9:38
 * @版本：1.0
 * @描述：
 */
public class SecurityFilterTest extends TestCase {

    private Directory dir;
    private IndexReader reader;
    private IndexSearcher searcher;

    @Override
    public void setUp() throws IOException {
        dir = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());
        IndexWriter writer = new IndexWriter(dir, config);
        Document doc1 = new Document();
        doc1.add(new StringField("owner", "elwood", Field.Store.YES));
        doc1.add(new TextField("keywords", "elwood's sensitive info", Field.Store.YES));
        writer.addDocument(doc1);
        Document doc2 = new Document();
        doc2.add(new StringField("owner", "jake", Field.Store.YES));
        doc2.add(new TextField("keywords", "jake's sensitive info", Field.Store.YES));
        writer.addDocument(doc2);
        writer.close();

        reader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(reader);
    }

    public void testSecurityFilter() throws IOException {
        TermQuery keywordsQuery = new TermQuery(new Term("keywords", "info"));
        System.out.println(TestUtil.hitCount(searcher, keywordsQuery)); // 2
        TermQuery ownerQuery = new TermQuery(new Term("owner", "jake"));
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(keywordsQuery, BooleanClause.Occur.MUST);
        builder.add(ownerQuery, BooleanClause.Occur.FILTER);
        Query query = builder.build();
        System.out.println(TestUtil.hitCount(searcher, query)); // 1
        TopDocs hits = searcher.search(query, 10);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            // jake's sensitive info
            System.out.println(searcher.doc(scoreDoc.doc).get("keywords"));
        }
    }
}
