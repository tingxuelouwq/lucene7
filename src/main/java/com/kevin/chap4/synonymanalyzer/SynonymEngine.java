package com.kevin.chap4.synonymanalyzer;

import java.io.IOException;

/**
 * @类名: SynonymEngine
 * @包名：com.kevin.chap4.synonymanalyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/12 15:05
 * @版本：1.0
 * @描述：
 */
public interface SynonymEngine {

    String[] getSynonyms(String s) throws IOException;
}
