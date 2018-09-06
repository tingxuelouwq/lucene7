package com.kevin.chap8.analyzer.ngram;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * @类名: NGramAnalyzer
 * @包名：com.kevin.chap8
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/8/3 9:19
 * @版本：1.0
 * @描述：
 */
public class NGramAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenStream target = new NGramTokenFilter(source, 2, 4);
        return new TokenStreamComponents(source, target);
    }
}
