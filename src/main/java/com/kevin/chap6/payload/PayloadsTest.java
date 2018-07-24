package com.kevin.chap6.payload;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * @类名: PayloadsTest
 * @包名：com.kevin.chap6.payload
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/24 18:56
 * @版本：1.0
 * @描述：
 */
public class PayloadsTest extends TestCase {

    private Directory dir;
    private Analyzer analyzer;

    @Override
    public void setUp() throws IOException {
        dir = new RAMDirectory();
        analyzer = new bulle
    }
}
