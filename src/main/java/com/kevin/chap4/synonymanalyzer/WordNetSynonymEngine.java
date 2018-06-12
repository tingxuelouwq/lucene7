package com.kevin.chap4.synonymanalyzer;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @类名: WordNetSynonymEngine
 * @包名：com.kevin.chap4.synonymanalyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/12 17:11
 * @版本：1.0
 * @描述：
 */
public class WordNetSynonymEngine implements SynonymEngine {

    private Directory directory;
    private IndexReader reader;
    private IndexSearcher searcher;

    public WordNetSynonymEngine(String path) throws IOException {
        directory = FSDirectory.open(Paths.get(path));
        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    public void close() throws IOException {
        reader.close();
        directory.close();
    }

    @Override
    public String[] getSynonyms(String word) throws IOException {
        List<String> synList = new ArrayList<>();

        return new String[0];
    }
}
