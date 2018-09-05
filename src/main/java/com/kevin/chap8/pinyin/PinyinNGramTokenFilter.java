package com.kevin.chap8.pinyin;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;

/**
 * 类名: PinyinNGramTokenFilter<br/>
 * 包名：com.kevin.chap8.pinyin<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/5 10:34<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class PinyinNGramTokenFilter extends TokenFilter {

    private CharTermAttribute termAtt;
    private OffsetAttribute offsetAtt;
    private int minGram;
    private int maxGram;

    private static final int DEFAULT_MIN_GRAM = 2;
    private static final int DEFAULT_MAX_GRAM = 5;

    public PinyinNGramTokenFilter(TokenStream input) {
        this(input, DEFAULT_MIN_GRAM, DEFAULT_MAX_GRAM);
    }

    public PinyinNGramTokenFilter(TokenStream input, int minGram, int maxGram) {
        super(input);
        termAtt = addAttribute(CharTermAttribute.class);
        offsetAtt = addAttribute(OffsetAttribute.class);
        if (minGram < 1) {
            throw new IllegalArgumentException("minGram must be greater than zero");
        }
        if (minGram > maxGram) {
            throw new IllegalArgumentException("minGram must not be greater than maxGram");
        }
        this.minGram = minGram;
        this.maxGram = maxGram;
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (input.incrementToken()) {
            return true;
        }
        return false;
    }

    private boolean containsChinese(String str) {
        if (str == null || "".equals(str.trim())) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (isChinese(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean isChinese(char ch) {
        int intCh = ch;
        return (intCh >= 19968 && intCh <= 40869);
    }
}
