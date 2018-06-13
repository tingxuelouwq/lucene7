package com.kevin.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

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

    private static void displayTokens(TokenStream tokenStream) throws IOException {
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            System.out.print("[" + charTermAttribute.toString() + "] ");
        }
        tokenStream.end();
        tokenStream.close();
    }

    public static void displayTokensWithFullDetails(Analyzer analyzer, String text) throws IOException {
        TokenStream tokenStream = analyzer.tokenStream("contents", new StringReader(text));
        CharTermAttribute charTermAttribute =
                tokenStream.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute positionIncrementAttribute =
                tokenStream.addAttribute(PositionIncrementAttribute.class);
        OffsetAttribute offsetAttribute =
                tokenStream.addAttribute(OffsetAttribute.class);
        TypeAttribute typeAttribute =
                tokenStream.addAttribute(TypeAttribute.class);
        tokenStream.reset();
        int position = 0;
        while (tokenStream.incrementToken()) {
            int increment = positionIncrementAttribute.getPositionIncrement();
            if (increment > 0) {
                position = position + increment;
                System.out.println();
                System.out.print(position + ": ");
            }
            System.out.println("[" +
                    charTermAttribute.toString() + ":" +
                    offsetAttribute.startOffset() + "->" +
                    offsetAttribute.endOffset() + ":" +
                    typeAttribute.type() + "]");
        }
        tokenStream.end();
        tokenStream.close();
    }

    public static void displayTokensWithPosition(Analyzer analyzer, String text) throws IOException {
        TokenStream tokenStream = analyzer.tokenStream("contents", new StringReader(text));
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute positionIncrementAttribute = tokenStream.addAttribute(PositionIncrementAttribute.class);
        tokenStream.reset();
        int position = 0;
        while (tokenStream.incrementToken()) {
            int increment = positionIncrementAttribute.getPositionIncrement();
            if (increment > 0) {
                position = position + increment;
                System.out.println();
                System.out.print(position + ": ");
            }
            System.out.print("[" + charTermAttribute.toString() + "] ");
        }
        System.out.println();
        tokenStream.end();
        tokenStream.close();
    }
}
