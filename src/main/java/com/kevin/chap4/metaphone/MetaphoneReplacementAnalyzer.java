package com.kevin.chap4.metaphone;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;

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
        return new TokenStreamComponents(source, new MetaphoneReplacementFilter(source));
    }
}
