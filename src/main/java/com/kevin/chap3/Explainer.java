package com.kevin.chap3;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @类名: Explainer
 * @包名：com.kevin.chap3
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/4 9:32
 * @版本：1.0
 * @描述：
 */
public class Explainer {
    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = "D:\\Lucene\\Index";
        String queryExpression = "junit";
        Directory directory = FSDirectory.open(Paths.get(indexDir));
        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query query = parser.parse(queryExpression);
        System.out.println("Query: " + queryExpression);

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(query, 10);
        for (ScoreDoc match : topDocs.scoreDocs) {
            Explanation explanation = searcher.explain(query, match.doc);
            System.out.println("----------");
            Document doc = searcher.doc(match.doc);
            System.out.println(doc.get("title"));
            System.out.println(explanation.toString());
        }

        reader.close();
        directory.close();
    }
}
