package com.kevin.chap8.highlighter;

import com.kevin.util.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * @类名: IndexHighlightTest
 * @包名：com.kevin.chap8.highlighter
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/8/25 22:56
 * @版本：1.0
 * @描述：
 */
public class IndexHighlightTest extends TestCase {

    public void testHits() throws IOException, InvalidTokenOffsetsException {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TermQuery query = new TermQuery(new Term("title", "action"));
        TopDocs hits = searcher.search(query, 10);

        QueryScorer scorer = new QueryScorer(query, "title");
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
        Highlighter highlighter = new Highlighter(scorer);
        highlighter.setTextFragmenter(fragmenter);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String title = doc.get("title");
            Fields fields = reader.getTermVectors(scoreDoc.doc);
            TokenStream stream = TokenSources.getTokenStream("title", fields, title,
                    new StandardAnalyzer(), highlighter.getMaxDocCharsToAnalyze() - 1);
            String fragment = highlighter.getBestFragment(stream, title);
            System.out.println(fragment);
        }
    }
}
