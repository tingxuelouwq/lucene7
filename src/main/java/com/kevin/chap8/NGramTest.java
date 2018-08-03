package com.kevin.chap8;

import com.kevin.util.AnalyzerUtils;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * @类名: NGramTest
 * @包名：com.kevin.chap8
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/8/3 9:19
 * @版本：1.0
 * @描述：
 */
public class NGramTest extends TestCase {

    public void testNGramTokenFilter24() throws IOException {
        AnalyzerUtils.displayTokensWithPosition(new NGramAnalyzer(), "lettuce");
    }

    public void testFrontEdgeNGramTokenFilter14() throws IOException {
        AnalyzerUtils.displayTokensWithPosition(new FrontEdgeNGrameAnalyzer(), "lettuce");
    }
}
