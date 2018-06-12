package com.kevin.chap4.synonymanalyzer;

import java.io.IOException;
import java.util.HashMap;

/**
 * @类名: TestSynonymEngine
 * @包名：com.kevin.chap4.synonymanalyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/12 15:06
 * @版本：1.0
 * @描述：
 */
public class TestSynonymEngine implements SynonymEngine {

    private static HashMap<String, String[]> map = new HashMap<>();

    static {
        map.put("quick", new String[]{"fast", "speed"});
        map.put("jumps", new String[]{"leaps", "hops"});
        map.put("over", new String[] {"above"});
        map.put("lazy", new String[] {"apathetic", "sluggish"});
        map.put("dog", new String[] {"canine", "pooch"});
    }

    @Override
    public String[] getSynonyms(String s) throws IOException {
        return map.get(s);
    }
}
