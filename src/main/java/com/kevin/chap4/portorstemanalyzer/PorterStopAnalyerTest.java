package com.kevin.chap4.portorstemanalyzer;

import com.kevin.util.AnalyzerUtils;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * @类名: PorterStopAnalyerTest
 * @包名：com.kevin.chap4.portorstemanalyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/13 15:42
 * @版本：1.0
 * @描述：
 */
public class PorterStopAnalyerTest extends TestCase {

    public void testPorterStopAnalyzer() throws IOException {
        AnalyzerUtils.displayTokensWithFullDetails(new PorterStopAnalyzer(),
                "The quick fox jumps over the lazy dog");
    }
}
