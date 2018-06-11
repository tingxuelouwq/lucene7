package com.kevin.chap4;

import com.kevin.util.AnalyzerUtils;
import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Test;

import java.io.IOException;

/**
 * @类名: AnalyzerDemo
 * @包名：com.kevin.chap4
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/10 16:26
 * @版本：1.0
 * @描述：
 */
public class AnalyzerDemo extends TestCase {

    private static final Analyzer[] analyzers = new Analyzer[] {
            new WhitespaceAnalyzer(),
            new SimpleAnalyzer(),
            new StopAnalyzer(),
            new StandardAnalyzer()
    };

    private static void analyze(String text) throws IOException {
        System.out.println("Analyzing \"" + text + "\"");
        for (Analyzer analyzer : analyzers) {
            String name = analyzer.getClass().getSimpleName();
            System.out.println(" " + name + ":");
            System.out.print(" ");
            AnalyzerUtils.displayTokens(analyzer, text);
            System.out.println("\n");
        }
    }

    @Test
    public void testDisplayTokens() throws IOException {
        String[] texts = {
                "The quick brown fox jumped over the lazy dog",
                "XY&Z Corporation - xyz@excample.com"
        };
        for (String text : texts) {
            analyze(text);
        }
    }

    @Test
    public void testDisplayTokensWithFullDetails() throws IOException {
//        String text = "The quick brown fox...";
        String text = "I’ll email you at xyz@example.com";
        AnalyzerUtils.displayTokensWithFullDetails(new StandardAnalyzer(), text);
    }
}
