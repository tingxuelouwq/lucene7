package com.kevin.chap6.payload;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.payloads.PayloadEncoder;

/**
 * @类名: PayloadAnalyzer
 * @包名：com.kevin.chap6.payload
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/24 19:32
 * @版本：1.0
 * @描述：
 */
public class PayloadAnalyzer extends Analyzer {

    private PayloadEncoder encoder;

    public PayloadAnalyzer(PayloadEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer src = new WhitespaceTokenizer();
        DelimitedPayloadTokenFilter tok = new DelimitedPayloadTokenFilter(src, '|', encoder);
        return new TokenStreamComponents(src, tok);
    }
}
