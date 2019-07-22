package com.kevin.chap8.spellcheck;

import junit.framework.TestCase;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Paths;

/**
 * 类名: SpellCheckTest<br/>
 * 包名：com.kevin.chap8.spellcheck<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/29 14:26<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class SpellCheckTest extends TestCase {

    private final String dicPath = "D:/lucene/dic.txt";
    private final String spellCheckDir = "D:/lucene/spellcheck";
    private SpellChecker spellChecker;

    public void testSpellCheck() throws IOException {
        String word = "明朝那些事";
        search(word, 5);
    }

    private void createIndex() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(spellCheckDir));
        spellChecker = new SpellChecker(dir);
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        spellChecker.indexDictionary(new PlainTextDictionary(Paths.get(dicPath)), config, true);
    }

    public void search(String word, int numSug) throws IOException {
        String[] suggests = spellChecker.suggestSimilar(word, numSug);
        for (String suggest : suggests) {
            System.out.println(suggest);
        }
    }

    @Override
    public void setUp() throws IOException {
        createIndex();
    }
}
