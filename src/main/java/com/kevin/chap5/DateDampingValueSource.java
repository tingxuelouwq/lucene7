package com.kevin.chap5;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;

import java.io.IOException;
import java.util.Map;

/**
 * @类名: DateDampingValueSource
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/12 14:42
 * @版本：1.0
 * @描述：自定义ValueSource，计算日期递减时的权重因子，日期越近，权重值越高
 */
public class DateDampingValueSource extends FieldCacheSource {

    private long now;    // 当前时间

    public DateDampingValueSource(String field) {
        super(field);
        now = System.currentTimeMillis();
    }

    @Override
    public FunctionValues getValues(Map context, LeafReaderContext readerContext)
            throws IOException {
        final NumericDocValues numericDocValues =
                DocValues.getNumeric(readerContext.reader(), field);
        return new FunctionValues() {
            @Override
            public float floatValue(int doc) {
                return
            }

            @Override
            public String toString(int doc) throws IOException {
                return null;
            }
        }
    }
}
