package com.kevin.chap6;

import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;

/**
 * @类名: DistanceComparatorSource
 * @包名：com.kevin.chap6
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/19 10:28
 * @版本：1.0
 * @描述：
 */
public class DistanceComparatorSource extends FieldComparatorSource {

    private int x;
    private int y;

    public DistanceComparatorSource(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public FieldComparator<?> newComparator(String fieldname, int numHits,
                                            int sortPos, boolean reversed) {
        return null;
    }
}
