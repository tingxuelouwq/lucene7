package com.kevin.chap8.analyzer.snowball;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * @类名: SnowballAnalyzer
 * @包名：com.kevin.chap8.analyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/8/26 18:05
 * @版本：1.0
 * @描述：
 */
public class SnowballAnalyzer extends Analyzer {

    private String name;

    public SnowballAnalyzer(String name) {
        this.name = name;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer src = new StandardTokenizer();
        TokenStream target = new SnowballFilter(src, name);
        return new TokenStreamComponents(src, target);
    }
}
