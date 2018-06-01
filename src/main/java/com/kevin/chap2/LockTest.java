package com.kevin.chap2;

import junit.framework.TestCase;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @类名: LockTest
 * @包名：com.kevin.chap2
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/5/31 10:24
 * @版本：1.0
 * @描述：
 */
public class LockTest extends TestCase {

    private Directory dir;

    @Override
    public void setUp() throws IOException {
        String indexDir = "D:\\Lucene\\Index";
        dir = FSDirectory.open(Paths.get(indexDir));
    }

    @Test
    public void testWriteLock() throws IOException {
        IndexWriter writer1 = new IndexWriter(dir, new IndexWriterConfig());
        IndexWriter writer2 = null;
        try {
            writer2 = new IndexWriter(dir, new IndexWriterConfig());
            fail("We should never reach this point");
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } finally {
            writer1.close();
            assertNull(writer2);
        }
    }
}
