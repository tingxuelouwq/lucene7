package com.kevin.chap8.pinyin;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;

/**
 * 类名: PinyinTokenFilter<br/>
 * 包名：com.kevin.chap8.pinyin<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/4 16:35<br/>
 * 版本：1.0<br/>
 * 描述：拼音过滤器，负责将汉字转换为拼音，参考<code>StopFilter</code>的实现<br/>
 */
public class PinyinTokenFilter extends TokenFilter {

    private CharTermAttribute termAtt;
    private PositionIncrementAttribute posIncrAtt;
    private int skippedPositions;
    private String term;
    /** 汉语拼音输出转换器，基于Pinyin4j **/
    private HanyuPinyinOutputFormat outputFormat;
    /** Term最小长度，小于这个长度的不进行拼音转换 **/
    private int minTermLength;

    private static final int DEFAULT_MIN_TERM_LENGTH = 2;

    public PinyinTokenFilter(TokenStream input) {
        this(input, DEFAULT_MIN_TERM_LENGTH);
    }

    public PinyinTokenFilter(TokenStream input, int minTermLength) {
        super(input);
        this.termAtt = addAttribute(CharTermAttribute.class);
        this.posIncrAtt = addAttribute(PositionIncrementAttribute.class);
        this.outputFormat = new HanyuPinyinOutputFormat();
        this.outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        this.outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        this.minTermLength = minTermLength;
        if (this.minTermLength < DEFAULT_MIN_TERM_LENGTH) {
            this.minTermLength = DEFAULT_MIN_TERM_LENGTH;
        }
    }

    @Override
    public boolean incrementToken() throws IOException {
        skippedPositions = 0;
        while (input.incrementToken()) {
            term = termAtt.toString();
            if (containsChinese(term) && term.length() >= minTermLength) {
                try {
                    String pinyinTerm = getPinyinString(term);
                    termAtt.copyBuffer(pinyinTerm.toCharArray(), 0, pinyinTerm.length());
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
                if (skippedPositions != 0) {
                    posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement() + skippedPositions);
                }
                return true;
            }
            skippedPositions += posIncrAtt.getPositionIncrement();
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

    private String getPinyinString(String str) throws BadHanyuPinyinOutputFormatCombination {
        return PinyinHelper.toHanYuPinyinString(str, outputFormat, "", true);
    }
}
