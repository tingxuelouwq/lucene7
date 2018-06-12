package com.kevin.chap4.metaphone;

import org.apache.commons.codec.language.Metaphone;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;

/**
 * @类名: MetaphoneReplacementFileter
 * @包名：com.kevin.chap4
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/12 9:28
 * @版本：1.0
 * @描述：
 */
public class MetaphoneReplacementFilter extends TokenFilter {

    private static final String METAPHONE = "metaphone";

    private Metaphone metaphone = new Metaphone();
    private CharTermAttribute charTermAttribute;
    private TypeAttribute typeAttribute;

    public MetaphoneReplacementFilter(TokenStream input) {
        super(input);
        charTermAttribute = addAttribute(CharTermAttribute.class);
        typeAttribute = addAttribute(TypeAttribute.class);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }

        String encoded = metaphone.encode(charTermAttribute.toString());
        charTermAttribute.setEmpty();
        charTermAttribute.append(encoded);
        typeAttribute.setType(METAPHONE);
        return true;
    }
}
