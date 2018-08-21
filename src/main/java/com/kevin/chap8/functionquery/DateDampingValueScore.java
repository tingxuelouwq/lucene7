package com.kevin.chap8.functionquery;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;

import java.io.IOException;
import java.util.Map;

/**
 * 类名: DateDampingValueScore<br/>
 * 包名：com.kevin.chap8.functionquery<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/21 10:02<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class DateDampingValueScore extends FieldCacheSource {

    private long now;

    public DateDampingValueScore(String field, long now) {
        super(field);
        this.now = now;
    }

    @Override
    public FunctionValues getValues(Map context, LeafReaderContext readerContext) throws IOException {
        final NumericDocValues values = getNumericDocValues(context, readerContext);
        return new FunctionValues() {
            int lastDocID;

            private long getValueForDoc(int doc) throws IOException {
                if (doc < lastDocID) {
                    throw new IllegalArgumentException("docs were sent out-of-order: lastDocID=" + lastDocID + " vs docID=" + doc);
                }
                lastDocID = doc;
                int curDocID = values.docID();
                if (doc > curDocID) {
                    curDocID = values.advance(doc);
                }
                if (doc == curDocID) {
                    return values.longValue();
                } else {
                    return 0;
                }
            }

            @Override
            public float floatVal(int doc) throws IOException {
                return ScoreUtil.getNewScoreFactor(now, getValueForDoc(doc));
            }

            @Override
            public String toString(int doc) throws IOException {
                return description() + "=" + floatVal(doc);
            }
        };
    }

    private NumericDocValues getNumericDocValues(Map context, LeafReaderContext readerContext) throws IOException {
        return DocValues.getNumeric(readerContext.reader(), field);
    }
}
