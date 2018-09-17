package com.kevin.chap8.facet;

import org.apache.lucene.facet.range.LongRange;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 类名: RangeFacetsTest<br/>
 * 包名：com.kevin.chap8.facet<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/17 16:49<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class RangeFacetsTest {

    private Directory indexDir = FSDirectory.open(Paths.get("D:/lucene/index"));
    private IndexSearcher searcher;

    // 当前毫秒数
    private long nowSec = System.currentTimeMillis();
    // 1小时之前的毫秒数
    private LongRange PAST_HOUR = new LongRange("Past hour", nowSec - 3600L,
            true, nowSec, true);
    // 6小时之前的毫秒数
    private LongRange PAST_SIX_HOURS = new LongRange("Past six hour", nowSec - 21600L,
            true, nowSec, true);
    // 24小时之前的毫秒数
    private LongRange PAST_DAY = new LongRange("Past day", nowSec - 86400L,
            true, nowSec, true);

    public RangeFacetsTest() throws IOException {
    }


}
