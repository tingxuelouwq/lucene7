package com.kevin.chap8.extquery;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.sandbox.queries.FuzzyLikeThisQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 类名: FuzzyLikeThisQueryTest<br/>
 * 包名：com.kevin.chap8.ext<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/28 14:19<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class FuzzyLikeThisQueryTest {

    public static void main(String[] args) throws IOException {
        String indexDir = "D:/lucene/index";
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        FuzzyLikeThisQuery flt = new FuzzyLikeThisQuery(3, new StandardAnalyzer());
        flt.addTerms("chilren  eternal", "title", 2, 0);
        TopDocs hits = searcher.search(flt, 10);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("title"));
        }
    }
}
