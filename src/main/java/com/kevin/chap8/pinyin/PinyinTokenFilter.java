package com.kevin.chap8.pinyin;

import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 * 类名: PinyinTokenFilter<br/>
 * 包名：com.kevin.chap8.pinyin<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/4 16:35<br/>
 * 版本：1.0<br/>
 * 描述：拼音过滤器，负责将汉字转换为拼音<br/>
 */
public class PinyinTokenFilter extends TokenFilter {

    private final CharTermAttribute termAtt;
    /** 汉语拼音输出转换器，基于Pinyin4j **/
    private HanyuPinyinOutputFormat outputFormat;
    /** 对于多音字会有多个拼音，fistChar表示只取第一个，否则会取多个拼音 **/
    private boolean firstChar;
    /** Term最小长度，小于这个长度的不进行拼音转换 **/
    private int minTermLength;
    private char[] curTermBuffer;
    private int curTermLength;
    private boolean ngramChinese;

    public PinyinTokenFilter(TokenStream input) {
        this(input, PinyinConstant.DEFAULT_FIRST_CHAR, PinyinConstant.DEFAULT_MIN_TERM_LENGTH, PinyinConstant.DEFAULT_NGRAM_CHINESE);
    }

    public PinyinTokenFilter(TokenStream input, boolean firstChar) {
        this(input, firstChar, PinyinConstant.DEFAULT_MIN_TERM_LENGTH, PinyinConstant.DEFAULT_NGRAM_CHINESE);
    }

    public PinyinTokenFilter(TokenStream input, boolean firstChar, int minTermLength) {
        this(input, firstChar, minTermLength, PinyinConstant.DEFAULT_NGRAM_CHINESE);
    }

    public PinyinTokenFilter(TokenStream input, boolean firstChar,
                             int minTermLength, boolean ngramChinese) {
        super(input);
        this.termAtt = addAttribute(CharTermAttribute.class);
        this.outputFormat = new HanyuPinyinOutputFormat();
        this.outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        this.outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        this.firstChar = firstChar;
        this.minTermLength = minTermLength;
        if (this.minTermLength < PinyinConstant.DEFAULT_MIN_TERM_LENGTH) {
            this.minTermLength = PinyinConstant.DEFAULT_MIN_TERM_LENGTH;
        }
        this.ngramChinese = ngramChinese;
    }

    @Override
    public boolean incrementToken() throws IOException {
        return false;
    }
}
