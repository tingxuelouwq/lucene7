package com.kevin.chap4;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;

import java.util.Set;

/**
 * @类名: StopAnalyzer2
 * @包名：com.kevin.chap4
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/11 16:00
 * @版本：1.0
 * @描述：
 */
public class StopAnalyzer2 extends Analyzer {

    private Set stopWords;

    public StopAnalyzer2() {
        stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }

    public StopAnalyzer2(String[] stopWords) {
        this.stopWords = StopFilter.makeStopSet(stopWords);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        return null;
    }
}
