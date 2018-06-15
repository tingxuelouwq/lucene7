package com.kevin.chap4.chineseanalyzer;

import com.kevin.util.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;

/**
 * @类名: ChineseDemo
 * @包名：com.kevin.chap4.chineseanalyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/14 11:12
 * @版本：1.0
 * @描述：
 */
public class ChineseDemo {

    private static final String[] strings = {"中华人民共和国在1949年建立，从此开始了新中国的伟大篇章。"};
    private static Analyzer[] analyzers = {
            new SimpleAnalyzer(),
            new StandardAnalyzer(),
            new CJKAnalyzer(),
            new SmartChineseAnalyzer(),
            new IKAnalyzer(true)
    };

    public static void main(String[] args) throws IOException {
        for (String string : strings) {
            for (Analyzer analyzer : analyzers) {
                System.out.println(analyzer.getClass().getSimpleName() + " : " + string);
                AnalyzerUtils.displayTokensWithFullDetails(analyzer, string);
                System.out.println("-------------");
            }
        }
    }
}
