package com.kevin.chap4.portorstemanalyzer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
/**
 * @类名: PorterStopAnalyzer
 * @包名：com.kevin.chap4.portorstemanalyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/13 11:08
 * @版本：1.0
 * @描述：
 */
public class PorterStopAnalyzer extends Analyzer {

    private static final CharArraySet stopWords =
            StopAnalyzer.ENGLISH_STOP_WORDS_SET;

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer src = new LowerCaseTokenizer();
        TokenStream tok = new StopFilter(src, stopWords);
        tok = new PorterStemFilter(tok);
        return new TokenStreamComponents(src, tok);
    }
}
