package com.kevin.chap6;

import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleFieldComparator;

import java.io.IOException;

/**
 * @类名: DistanceSourceLookupComparator
 * @包名：com.kevin.chap6
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/19 23:08
 * @版本：1.0
 * @描述：
 */
public class DistanceSourceLookupComparator extends SimpleFieldComparator<String> {

    /** 优先队列数组 **/
    private float[] values;
    /** 优先队列队首元素值 **/
    private float topValue;
    /** 优先队列队尾元素值 **/
    private float bottom;
    /** 排序域 **/
    private String field;
    /** 用于遍历docIDs的DocValue **/
    private BinaryDocValues currentReaderValues;

    private int x;
    private int y;

    public DistanceSourceLookupComparator(int numHits, String field, int x, int y) {
        values = new float[numHits];
        this.field = field;
        this.x = x;
        this.y = y;
    }

    @Override
    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        currentReaderValues = DocValues.getBinary(context.reader(), field);
    }

    @Override
    public int compare(int slot1, int slot2) {
        if (values[slot1] > values[slot2]) {
            return 1;
        } else if ()
        return 0;
    }

    @Override
    public void setTopValue(String value) {

    }

    @Override
    public String value(int slot) {
        return null;
    }

    @Override
    public void setBottom(int slot) throws IOException {

    }

    @Override
    public int compareBottom(int doc) throws IOException {
        return 0;
    }

    @Override
    public int compareTop(int doc) throws IOException {
        return 0;
    }

    @Override
    public void copy(int slot, int doc) throws IOException {

    }
}
