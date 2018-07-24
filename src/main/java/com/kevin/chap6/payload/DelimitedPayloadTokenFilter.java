package com.kevin.chap6.payload;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.PayloadEncoder;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;

import java.io.IOException;

/**
 * @类名: DelimitedPayloadTokenFilter
 * @包名：com.kevin.chap6.payload
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/24 19:05
 * @版本：1.0
 * @描述：
 */
public class DelimitedPayloadTokenFilter extends TokenFilter {

    public static final char DEFAULT_DELIMITER = '|';
    private final char delimiter;
    private final CharTermAttribute termAttr;
    private final PayloadAttribute payloadAttr;
    private final PayloadEncoder encoder;

    protected DelimitedPayloadTokenFilter(TokenStream input, char delimiter, PayloadEncoder encoder) {
        super(input);
        this.delimiter = delimiter;
        this.termAttr = addAttribute(CharTermAttribute.class);
        this.payloadAttr = addAttribute(PayloadAttribute.class);
        this.encoder = encoder;
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            final char[] buffer = termAttr.buffer();
            final int length = termAttr.length();
            for (int i = 0; i < length; i++) {
                if (buffer[i] == delimiter) {
                    payloadAttr.setPayload(encoder.encode(buffer, i + 1, (length - (i + 1))));
                    termAttr.setLength(i);  // set a new length
                    return true;
                }
            }
            // we have not seen the delimiter
            payloadAttr.setPayload(null);
            return true;
        }
        return false;
    }
}
