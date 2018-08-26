package com.kevin.chap8.analyzer.shingle;

import com.kevin.util.AnalyzerUtils;
import junit.framework.TestCase;

/**
 * @类名: ShingleTest
 * @包名：com.kevin.chap8.analyzer.shingle
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/8/26 23:50
 * @版本：1.0
 * @描述：
 */
public class ShingleTest extends TestCase {

    public void testShingle() {
        String str = "please divide this sentence into shingles";
        AnalyzerUtils.display(str, new ShingleAnalyzer());
    }
}
