package com.kevin.chap4.stopanalyzer;

import com.kevin.util.AnalyzerUtils;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;

import java.io.IOException;

/**
 * @类名: StopAnalyzer2
 * @包名：com.kevin.chap4
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/11 16:00
 * @版本：1.0
 * @描述：
 */
public class StopAnalyzer2 extends Analyzer {

    private CharArraySet stopWords;

    public StopAnalyzer2() {
        stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }

    public StopAnalyzer2(String[] stopWords) {
        this.stopWords = StopFilter.makeStopSet(stopWords);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new LetterTokenizer();
        TokenStream tok = new LowerCaseFilter(source);
        tok = new StopFilter(tok, StandardAnalyzer.ENGLISH_STOP_WORDS_SET);
        return new TokenStreamComponents(source, tok);
    }

    public static void main(String[] args) throws IOException {
        AnalyzerUtils.displayTokensWithFullDetails(new StopAnalyzer2(), "The quick brown...");
    }
}
