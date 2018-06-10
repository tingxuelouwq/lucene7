package com.kevin.chap4;

import com.kevin.util.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * @类名: AnalyzerDemo
 * @包名：com.kevin.chap4
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/10 16:26
 * @版本：1.0
 * @描述：
 */
public class AnalyzerDemo {

    private static final String[] examples = {
            "Thie quick brown fox jumped over the lazy dog",
            "XY&Z Corporation - xyz@excample.com"
    };

    private static final Analyzer[] analyzers = new Analyzer[] {
            new WhitespaceAnalyzer(),
            new SimpleAnalyzer(),
            new StopAnalyzer(),
            new StandardAnalyzer()
    };

    public static void main(String[] args) {
        String[] strings = examples;
        if (args.length > 0) {
            strings = args;
        }
        for (String text : strings) {
            analyze(text);
        }
    }

    private static void analyze(String text) {
        System.out.println("Analyzing \"" + text + "\"");
        for (Analyzer analyzer : analyzers) {
            String name = analyzer.getClass().getSimpleName();
            System.out.println(" " + name + ":");
            System.out.print(" ");
            AnalyzerUtils.displayTokens(analyzer, text);
            System.out.println("\n");
        }
    }
}
