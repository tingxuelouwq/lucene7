package com.kevin.chap8.analyzer.snowball;

import com.kevin.util.AnalyzerUtils;
import junit.framework.TestCase;

/**
 * @类名: SnowballTest
 * @包名：com.kevin.chap8.analyzer.snowball
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/8/26 18:09
 * @版本：1.0
 * @描述：
 */
public class SnowballTest extends TestCase {

    /**
     * 1:[stem]:0->8:<ALPHANUM>
     * 1:[algorithm]:9->19:<ALPHANUM>
     */
    public void testEnglish() {
        AnalyzerUtils.display("stemming algorithms", new SnowballAnalyzer("English"));
    }

    /**
     * 1:[algoritm]:0->10:<ALPHANUM>
     */
    public void testSpanish() {
        AnalyzerUtils.display("algoritmos", new SnowballAnalyzer("Spanish"));
    }
}
