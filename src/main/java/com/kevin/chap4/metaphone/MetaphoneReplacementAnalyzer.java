package com.kevin.chap4.metaphone;

import org.apache.commons.codec.language.Metaphone;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.phonetic.PhoneticFilter;

/**
 * @类名: MetaphoneReplacementAnalyzer
 * @包名：com.kevin.chap4
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/12 9:26
 * @版本：1.0
 * @描述：
 */
public class MetaphoneReplacementAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new LetterTokenizer();
        TokenStream tok = new MetaphoneReplacementFilter(source);
//        TokenStream tok = new PhoneticFilter(source, new Metaphone(), false);
        return new TokenStreamComponents(source, tok);
    }
}
