package com.kevin.chap8.highlighter;

import junit.framework.TestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;

import java.io.IOException;
import java.io.StringReader;

/**
 * 类名: CSSHighlightTest<br/>
 * 包名：com.kevin.chap8.highlighter<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/24 16:42<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class CSSHighlightTest extends TestCase {

    private static final String text = "In this section we'll show you how to make the simplest " +
            "programmatic query, searching for a single term, and then " +
            "we'll see how to use QueryParser to accept textual queries. " +
            "In the sections that follow, we’ll take this simple example " +
            "further by detailing all the query types built into Lucene. " +
            "We begin with the simplest search of all: searching for all " +
            "documents that contain a single term.";

    public void testHighlight() throws ParseException, IOException, InvalidTokenOffsetsException {
        QueryParser parser = new QueryParser("field", new StandardAnalyzer());
        Query query = parser.parse("term");
        TokenStream tokenStream = new StandardAnalyzer()
                .tokenStream("field", new StringReader(text));
        QueryScorer scorer = new QueryScorer(query, "field");
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
        Formatter formatter = new SimpleHTMLFormatter("<span class=\"highlight\">",
                "</span>");
        Highlighter highlighter = new Highlighter(formatter, scorer);
        highlighter.setTextFragmenter(fragmenter);
        String result = highlighter.getBestFragments(tokenStream, text, 3, "...");
        StringBuilder builder = new StringBuilder();
        builder.append("<html>\n").append("<style>\n")
                .append(".highlight { background: yellow;}\n")
                .append("</style>\n")
                .append("<body>\n")
                .append(result)
                .append("\n</body>\n</html>");
        System.out.println(builder.toString());
    }
}
