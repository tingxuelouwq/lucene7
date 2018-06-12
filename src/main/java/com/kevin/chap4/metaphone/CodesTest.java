package com.kevin.chap4.metaphone;

import junit.framework.TestCase;
import org.apache.commons.codec.language.Metaphone;

/**
 * @类名: CodesTest
 * @包名：com.kevin.chap4
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/12 9:56
 * @版本：1.0
 * @描述：
 */
public class CodesTest extends TestCase {

    public void testMetaphone() {
        Metaphone metaphone = new Metaphone();
        System.out.println(metaphone.encode("cute"));   // KT
        System.out.println(metaphone.encode("cat"));    // KT
    }
}
