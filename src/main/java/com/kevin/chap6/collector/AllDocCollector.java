package com.kevin.chap6.collector;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名: AllDocCollector<br/>
 * 包名：com.kevin.chap4.synonymanalyzer<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/21 9:53<br/>
 * 版本：1.0<br/>
 * 描述：记录各个匹配的文档<br/>
 */
public class AllDocCollector implements Collector, LeafCollector {

    private List<ScoreDoc> docs = new ArrayList<>();
    private Scorer scorer;

    @Override
    public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
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
        docs.add(new ScoreDoc(doc, scorer.score()));
    }

    public List<ScoreDoc> getHits() {
        return docs;
    }
}
