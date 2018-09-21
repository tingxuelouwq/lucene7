package com.kevin.chap6.collector;

import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.Scorer;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @类名: BookLinkCollector
 * @包名：com.kevin.chap6.collector
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/21 15:23
 * @版本：1.0
 * @描述：为所有唯一的URL及其对应的书籍标题创建一个HashMap对象
 */
public class BookLinkCollector implements Collector, LeafCollector {

    /** 评分计算器 **/
    private Scorer scorer;
    private SortedDocValues titleDocValues;
    private SortedDocValues urlDocValues;
    private Map<String, String> documents = new HashMap<>();

    @Override
    public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
        titleDocValues = DocValues.getSorted(context.reader(), "title");
        urlDocValues = DocValues.getSorted(context.reader(), "url");
        return this;
    }

    @Override
    public boolean needsScores() {
        return true;
    }

    @Override
    public void setScorer(Scorer scorer) throws IOException {
        this.scorer = scorer;
    }

    @Override
    public void collect(int doc) throws IOException {
        if (titleDocValues.advanceExact(doc) && urlDocValues.advanceExact(doc)) {
            String url = urlDocValues.binaryValue().utf8ToString();
            String title = titleDocValues.binaryValue().utf8ToString();
            documents.put(url, title);
            System.out.println(title + ":" + scorer.score());
        }
    }

    public Map<String, String> getLinks() {
        return Collections.unmodifiableMap(documents);
    }
}
