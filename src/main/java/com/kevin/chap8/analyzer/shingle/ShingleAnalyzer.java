package com.kevin.chap8.analyzer.shingle;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.shingle.ShingleFilter;

/**
 * @类名: ShingleAnalyzer
 * @包名：com.kevin.chap8.analyzer.shingle
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/8/26 23:47
 * @版本：1.0
 * @描述：
 */
public class ShingleAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer src = new LowerCaseTokenizer();
        TokenStream target = new ShingleFilter(src);
        return new TokenStreamComponents(src, target);
    }
}
