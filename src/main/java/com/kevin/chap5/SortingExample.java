package com.kevin.chap5;

import com.kevin.util.TestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;

/**
 * @类名: SortingExample
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/15 16:46
 * @版本：1.0
 * @描述：
 */
public class SortingExample {

    private Directory directory;

    public SortingExample(Directory directory) {
        this.directory = directory;
    }

    public void displayResults(Query query, Sort sort) throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs results = searcher.search(query, 20, sort);
        System.out.println("\nResults for: " +
                query.toString() + " sorted by " + sort);
        System.out.println(StringUtils.rightPad("Title", 30) +
                StringUtils.rightPad("pubmonth", 10) +
                StringUtils.center("id", 4) +
                StringUtils.center("score", 15));
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        DecimalFormat scoreFormatter = new DecimalFormat("0.######");
        for (ScoreDoc sd : results.scoreDocs) {
            int docId = sd.doc;
            float score = sd.score;
            Document doc = searcher.doc(docId);
            System.out.println(StringUtils.rightPad(
                    StringUtils.abbreviate(doc.get("title"), 29), 30) +
                    StringUtils.rightPad(doc.get("pubmonth"), 10) +
                    StringUtils.center("" + docId, 4) +
                    StringUtils.leftPad(scoreFormatter.format(score), 12));
        }
    }

    public static void main(String[] args) throws ParseException, IOException {
        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query allBooks = new MatchAllDocsQuery();
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(allBooks, BooleanClause.Occur.SHOULD);
        builder.add(parser.parse("java OR action"), BooleanClause.Occur.SHOULD);
        Query query = builder.build();

        Directory directory = TestUtil.getBookIndexDirectory();
        SortingExample example = new SortingExample(directory);
        example.displayResults(query, Sort.RELEVANCE);
        example.displayResults(query, Sort.INDEXORDER);

    }
}
