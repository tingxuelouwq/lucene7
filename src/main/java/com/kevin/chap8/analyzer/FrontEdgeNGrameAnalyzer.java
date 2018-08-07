package com.kevin.chap8.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;

/**
 * @类名: FrontEdgeNGrameAnalyzer
 * @包名：com.kevin.chap8
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/8/3 9:26
 * @版本：1.0
 * @描述：
 */
public class FrontEdgeNGrameAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new KeywordTokenizer();
        TokenStream target = new EdgeNGramTokenFilter(source, 1, 4);
        return new TokenStreamComponents(source, target);
    }
}
