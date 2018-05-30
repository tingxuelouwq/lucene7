package com.kevin.util;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @类名: TestUtil
 * @包名：com.kevin.util
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/25 9:56
 * @版本：1.0
 * @描述：
 */
public class TestUtil {

    public static long hitCount(IndexSearcher searcher, Query query) throws IOException {
        return searcher.search(query, 1).totalHits;
    }

    public static Directory getBookIndexDirectory() throws IOException {
        return FSDirectory.open(Paths.get("D:\\Lucene\\Index"));
    }
}
