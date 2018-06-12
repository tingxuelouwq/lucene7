package com.kevin.chap4.synonymanalyzer;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.Stack;

/**
 * @类名: MySynonymFilter
 * @包名：com.kevin.chap4.synonymanalyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/12 15:08
 * @版本：1.0
 * @描述：
 */
public class MySynonymFilter extends TokenFilter {

    private static final String TOKEN_TYPE_SYNONYM = "SYNONYM";
    private Stack<String> synonymStack;
    private SynonymEngine engine;
    private AttributeSource.State current;
    private CharTermAttribute charTermAttribute;
    private PositionIncrementAttribute positionIncrementAttribute;
    private TypeAttribute typeAttribute;

    protected MySynonymFilter(TokenStream input, SynonymEngine engine) {
        super(input);
        this.synonymStack = new Stack<>();
        this.engine = engine;
        this.charTermAttribute = addAttribute(CharTermAttribute.class);
        this.positionIncrementAttribute = addAttribute(PositionIncrementAttribute.class);
        this.typeAttribute = addAttribute(TypeAttribute.class);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!synonymStack.isEmpty()) {
            String syn = synonymStack.pop();
            restoreState(current);
            charTermAttribute.setEmpty();
            charTermAttribute.append(syn);
            positionIncrementAttribute.setPositionIncrement(0);
            typeAttribute.setType(TOKEN_TYPE_SYNONYM);
            return true;
        }

        if (!input.incrementToken()) {
            return false;
        }

        if (addAliasesToStack()) {
            current = captureState();
        }

        return true;
    }

    private boolean addAliasesToStack() throws IOException {
        String[] synonyms = engine.getSynonyms(charTermAttribute.toString());
        if (synonyms == null) {
            return false;
        }
        for (String synonym : synonyms) {
            synonymStack.push(synonym);
        }
        return true;
    }
}
