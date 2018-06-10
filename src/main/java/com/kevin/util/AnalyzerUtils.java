package com.kevin.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TermFrequencyAttribute;

import java.io.IOException;
import java.io.StringReader;

/**
 * @类名: AnalyzerUtils
 * @包名：com.kevin.util
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/10 16:33
 * @版本：1.0
 * @描述：
 */
public class AnalyzerUtils {

    public static void displayTokens(Analyzer analyzer, String text) throws IOException {
        displayTokens(analyzer.tokenStream("contents", new StringReader(text)));
    }

    private static void displayTokens(TokenStream stream) throws IOException {
        CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
        while (stream.incrementToken()) {
            System.out.print("[" + term. + "] ");
        }
    }
}
