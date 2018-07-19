package com.kevin.chap6.sort;

import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleFieldComparator;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @类名: DateValComparator
 * @包名：com.kevin.chap6.sort
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/19 15:07
 * @版本：1.0
 * @描述：
 */
public class DateValComparator extends SimpleFieldComparator<String> {

    /** 存储排序的数据集 **/
    private String[] values;
    /** 排序字段 **/
    private String field;
    /** 优先队列尾部数据 **/
    private String bottom;
    /** 优先队列头部数据 **/
    private String topValue;
    private BinaryDocValues currentReaderValues;
    private BytesRef missingValue;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 构造函数
     * @param numHits   设定的结果集中元素个数
     * @param field     排序字段
     */
    public DateValComparator(int numHits, String field, BytesRef missingValue) {
        this.values = new String[numHits];
        this.field = field;
        this.missingValue = missingValue != null ? missingValue : new BytesRef("");
    }

    /**
     * 第三步：自定义日期排序，如果优先队列未满，则调用该方法进行比较 <br/>
     * 如果slot1小于slot2，返回一个负数；<br/>如果slot1大于slot2，返回一个正数；<br/>如果相等，返回0
     * @param slot1
     * @param slot2
     * @return
     */
    @Override
    public int compare(int slot1, int slot2) {
        try {
            Date val1 = format.parse(values[slot1]);
            Date val2 = format.parse(values[slot2]);
            return compare(val1, val2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setTopValue(String value) {
        this.topValue = value;
    }

    @Override
    public String value(int slot) {
        return values[slot];
    }

    /**
     * 第一步：Lucene首先调用此方法进行数据的初始化
     * @param context
     * @throws IOException
     */
    @Override
    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        currentReaderValues = DocValues.getBinary(context.reader(), field);
    }

    @Override
    public void setBottom(int slot) throws IOException {
        this.bottom = values[slot];
    }

    /**
     * 如果优先队列已满，则调用该方法
     * @param doc
     * @return
     * @throws IOException
     */
    @Override
    public int compareBottom(int doc) throws IOException {
        try {
            Date val1 = format.parse(this.bottom);
            Date val2 = format.parse(getValueForDoc(doc).utf8ToString());
            return compare(val1, val2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int compare(Date val1, Date val2) {
        if (null == val1) {
            if (null == val2) {
                return 0;
            }
            return -1;
        } else if (null == val2) {
            return 1;
        } else {
            return val1.compareTo(val2);
        }
    }

    @Override
    public int compareTop(int doc) throws IOException {
        try {
            Date val1 = format.parse(this.topValue);
            Date val2 = format.parse(getValueForDoc(doc).utf8ToString());
            return compare(val1, val2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 第二步：将currentReaderValues中对应doc的值拷贝到values数组的对应位置
     * @param slot
     * @param doc
     * @throws IOException
     */
    @Override
    public void copy(int slot, int doc) throws IOException {
        values[slot] = getValueForDoc(doc).utf8ToString();
    }

    private BytesRef getValueForDoc(int doc) throws IOException {
        if (currentReaderValues.advanceExact(doc)) {
            return currentReaderValues.binaryValue();
        } else {
            return missingValue;
        }
    }
}
