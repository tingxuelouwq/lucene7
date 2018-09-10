package com.kevin.chap8.pinyin;

import com.kevin.util.AnalyzerUtils;
import org.apache.lucene.analysis.*;
import org.wltea.analyzer.lucene.IKTokenizer;

import java.io.IOException;

/**
 * 类名: PinyinAnalyzer<br/>
 * 包名：com.kevin.chap8.pinyin<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/5 10:11<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class PinyinAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer src = new IKTokenizer(false);
        TokenStream tok = new PinyinTokenFilter(src);
        tok = new PinyinNGramTokenFilter(tok);
        return new TokenStreamComponents(src, tok);
    }

    public static void main(String[] args) throws IOException {
        String text = "2011年3月31日，孙燕姿与相恋5年多的男友纳迪姆在新加坡登记结婚";
        Analyzer analyzer = new PinyinAnalyzer();
        AnalyzerUtils.displayTokensWithFullDetails(analyzer, text);
    }
}
