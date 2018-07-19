package com.kevin.chap6.sort;

import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;

/**
 * @类名: DateValComparatorSource
 * @包名：com.kevin.chap6.sort
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/19 15:42
 * @版本：1.0
 * @描述：
 */
public class DateValComparatorSource extends FieldComparatorSource {
    @Override
    public FieldComparator<?> newComparator(String fieldname, int numHits, int sortPos, boolean reversed) {
        System.out.println(fieldname);
        System.out.println(numHits);
        System.out.println(sortPos);
        System.out.println(reversed);
        return new DateValComparator(numHits, fieldname, null);
    }
}
