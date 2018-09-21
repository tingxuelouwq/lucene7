package com.kevin.chap4.synonymanalyzer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * @类名: SynonymAnalyzer
 * @包名：com.kevin.chap4.synonymanalyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/12 15:00
 * @版本：1.0
 * @描述：
 */
public class SynonymAnalyzer extends Analyzer {

    private static final CharArraySet stopWords =
            StopAnalyzer.ENGLISH_STOP_WORDS_SET;

    private SynonymEngine engine;

    public SynonymAnalyzer(SynonymEngine engine) {
        this.engine = engine;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final StandardTokenizer src = new StandardTokenizer();
        TokenStream tok = new StandardFilter(src);
        tok = new LowerCaseFilter(tok);
        tok = new StopFilter(tok, stopWords);
        tok = new MySynonymFilter(tok, engine);
        return new TokenStreamComponents(src, tok);
    }
}
