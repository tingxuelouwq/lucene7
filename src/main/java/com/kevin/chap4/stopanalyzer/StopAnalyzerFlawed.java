package com.kevin.chap4.stopanalyzer;

import com.kevin.util.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;

import java.io.IOException;

/**
 * @类名: StopAnalyzerFlawed
 * @包名：com.kevin.chap4
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/12 9:19
 * @版本：1.0
 * @描述：
 */
public class StopAnalyzerFlawed extends Analyzer {

    private CharArraySet stopWords;

    public StopAnalyzerFlawed() {
        stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new LetterTokenizer();
        return new TokenStreamComponents(source, new StopFilter(source, stopWords));
    }

    public static void main(String[] args) throws IOException {
        AnalyzerUtils.displayTokens(new StopAnalyzerFlawed(), "The quick brown...");
    }
}
