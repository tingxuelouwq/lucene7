package com.kevin.chap6.sort;

import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleFieldComparator;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

/**
 * @类名: DistanceSourceLookupComparator
 * @包名：com.kevin.chap6
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/19 23:08
 * @版本：1.0
 * @描述：
 */
public class DistanceSourceLookupComparator extends SimpleFieldComparator<Float> {

    /** 排序域的域值构成的数组 **/
    private float[] values;
    /** 优先队列队首元素值，即最小值 **/
    private float topValue;
    /** 优先队列队尾元素值，即最大值 **/
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

    /**
     * 第一步：Lucene首先调用该方法进行数据初始化，通过currentReaderValues来迭代数据
     * @param context
     * @throws IOException
     */
    @Override
    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        currentReaderValues = DocValues.getBinary(context.reader(), field);
    }

    /**
     * 第三步：自定义排序，如果如果优先队列未满，则调用该方法进行比较 <br/>
     * 如果slot1小于slot2，返回一个负数；<br/>如果slot1大于slot2，返回一个正数；<br/>如果相等，返回0
     * @param slot1
     * @param slot2
     * @return
     */
    @Override
    public int compare(int slot1, int slot2) {
        return Float.compare(values[slot1], values[slot2]);
    }

    @Override
    public void setTopValue(Float value) {
        topValue = value;
    }

    @Override
    public Float value(int slot) {
        return values[slot];
    }

    @Override
    public void setBottom(int slot) throws IOException {
        bottom = values[slot];
    }

    @Override
    public int compareBottom(int doc) throws IOException {
        float distance = getDistance(doc);
        return Float.compare(bottom, distance);
    }

    @Override
    public int compareTop(int doc) throws IOException {
        float distance = getDistance(doc);
        return Float.compare(topValue, distance);
    }

    /**
     * 第二步：将currentReaderValues中的元素值copy到values数组对应位置中。第一次会将currentReaderValues的
     * 前两个元素填充到values数组中，之后每次添加一个元素到values数组中
     * @param slot
     * @param doc
     * @throws IOException
     */
    @Override
    public void copy(int slot, int doc) throws IOException {
        values[slot] = getDistance(doc);
    }

    private BytesRef getValueForDoc(int doc) throws IOException {
        if (currentReaderValues.advanceExact(doc)) {
            return currentReaderValues.binaryValue();
        } else {
            return new BytesRef(Float.MAX_VALUE + "");
        }
    }

    /**
     * 求两点连线之间的距离
     * @param doc
     * @return
     */
    private float getDistance(int doc) throws IOException {
        BytesRef bytesRef = getValueForDoc(doc);
        String xy = bytesRef.utf8ToString();
        String[] array = xy.split(",");
        // 求横纵坐标差
        int deltaX = Integer.parseInt(array[0]) - x;
        int deltaY = Integer.parseInt(array[1]) - y;
        // 开平方根
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        System.out.println(distance);
        return distance;
    }
}
