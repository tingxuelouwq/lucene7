package com.kevin.chap8.highlighter;

import junit.framework.TestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.*;

import java.io.IOException;
import java.io.StringReader;

/**
 * 类名: SimpleHighlightTest<br/>
 * 包名：com.kevin.chap8.highlighter<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/24 16:22<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class SimpleHighlightTest extends TestCase {

    public void testHighlighter() throws IOException, InvalidTokenOffsetsException {
        String text = "The quick brown fox jumps over the lazy dog";
        TermQuery query = new TermQuery(new Term("field", "fox"));
        TokenStream tokenStream = new SimpleAnalyzer()
                .tokenStream("field", new StringReader(text));
        QueryScorer scorer = new QueryScorer(query, "field");
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
        Highlighter highlighter = new Highlighter(scorer);
        highlighter.setTextFragmenter(fragmenter);
        String result = highlighter.getBestFragment(tokenStream, text);
        System.out.println(result); // The quick brown <B>fox</B> jumps over the lazy dog
    }
}
