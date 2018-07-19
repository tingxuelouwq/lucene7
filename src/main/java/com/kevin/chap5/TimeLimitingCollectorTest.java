package com.kevin.chap5;

import com.kevin.util.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Counter;

import javax.naming.TimeLimitExceededException;
import java.io.IOException;

/**
 * @类名: TimeLimitingCollectorTest
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/19 9:15
 * @版本：1.0
 * @描述：
 */
public class TimeLimitingCollectorTest extends TestCase {

    public void testTimeLimitingCollector() throws IOException {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector topDocs = TopScoreDocCollector.create(10);
        TimeLimitingCollector collector = new TimeLimitingCollector(topDocs,
                Counter.newCounter(), 1000);
        Query query = new MatchAllDocsQuery();
        try {
            searcher.search(query, collector);
            System.out.println(topDocs.getTotalHits());
        } catch (TimeLimitingCollector.TimeExceededException e) {
            System.out.println("Too much time taken.");
        }
        reader.close();
        dir.close();
    }
}
