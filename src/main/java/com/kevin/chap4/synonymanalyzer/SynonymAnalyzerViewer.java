package com.kevin.chap4.synonymanalyzer;

import com.kevin.util.AnalyzerUtils;

import java.io.IOException;

/**
 * @类名: SynonymAnalyzerViewer
 * @包名：com.kevin.chap4.synonymanalyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/12 17:07
 * @版本：1.0
 * @描述：
 */
public class SynonymAnalyzerViewer {

    public static void main(String[] args) throws IOException {
        SynonymEngine engine = new TestSynonymEngine();
        AnalyzerUtils.displayTokensWithPosition(new SynonymAnalyzer(engine),
                "The quick brown fox jumps over the lazy dog");
    }
}
