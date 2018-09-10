package com.kevin.chap8.pinyin;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;

/**
 * 类名: PinyinNGramTokenFilter<br/>
 * 包名：com.kevin.chap8.pinyin<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/5 10:34<br/>
 * 版本：1.0<br/>
 * 描述：对转换后的拼音进行ngram处理的TokenFilter，参考<code>NGramTokenFitler和EdgeNGramTokenFilter</code>的实现<br/>
 */
public class PinyinNGramTokenFilter extends TokenFilter {

    private CharTermAttribute termAtt;
    private PositionIncrementAttribute posIncrAtt;
    /** gram最小值 **/
    private int minGram;
    /** gram最大值 **/
    private int maxGram;
    /** 缓存每一个token的数组 **/
    private char[] curTermBuffer;
    /** 缓存数组的长度 **/
    private int curTermLength;
    /** 当前gram大小 **/
    private int curGramSize;
    /** 位置增量 **/
    private int curPosInc;
    private State state;

    private static final int DEFAULT_MIN_GRAM = 2;
    private static final int DEFAULT_MAX_GRAM = 20;

    public PinyinNGramTokenFilter(TokenStream input) {
        this(input, DEFAULT_MIN_GRAM, DEFAULT_MAX_GRAM);
    }

    public PinyinNGramTokenFilter(TokenStream input, int minGram, int maxGram) {
        super(input);
        termAtt = addAttribute(CharTermAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);
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
        while (true) {
            if (curTermBuffer == null) {
                if (!input.incrementToken()) {
                    return false;
                } else {
                    curTermBuffer = termAtt.buffer().clone();
                    curTermLength = termAtt.length();
                    curGramSize = minGram;
                    curPosInc = posIncrAtt.getPositionIncrement();
                    state = captureState();
                }
            }

            if (curGramSize <= maxGram && curGramSize <= curTermLength) {
                restoreState(state);
                termAtt.copyBuffer(curTermBuffer, 0, curGramSize);
                posIncrAtt.setPositionIncrement(curPosInc);
                curPosInc = 0;
                curGramSize++;
                return true;
            }
            curTermBuffer = null;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        curTermBuffer = null;
    }
}
