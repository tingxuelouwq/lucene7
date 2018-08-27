package com.kevin.chap8.spellcheck;

import org.apache.lucene.search.spell.LevenshteinDistance;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 类名: SpellCheckerExample<br/>
 * 包名：com.kevin.chap8.spellcheck<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/27 15:14<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class SpellCheckerExample {

    public static void main(String[] args) throws IOException {
        String spellCheckDir = "D:\\lucene\\spellcheck";
        String wordToRespell = "letuce";

        Directory dir = FSDirectory.open(Paths.get(spellCheckDir));
        SpellChecker spell = new SpellChecker(dir);
        spell.setStringDistance(new LevenshteinDistance());
        String[] suggestions = spell.suggestSimilar(wordToRespell, 5);
        System.out.println(suggestions.length + " suggestions for '" +
                wordToRespell + "':");
        for (String suggestion : suggestions) {
            System.out.println(" " + suggestion);
        }
    }
}
