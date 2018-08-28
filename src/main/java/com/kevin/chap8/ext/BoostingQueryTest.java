package com.kevin.chap8.ext;

import com.kevin.util.TestUtil;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * 类名: BoostingQueryTest<br/>
 * 包名：com.kevin.chap8.ext<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/28 15:13<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class BoostingQueryTest {

    public static void main(String[] args) throws IOException {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        PrefixQuery positiveQuery = new PrefixQuery(new Term("category",
                "/technology/computers/programming"));
        TermQuery negativeQuery = new TermQuery(new Term("category",
                "/technology/computers/programming/education"));
        Query query = FunctionScoreQuery.boostByQuery(positiveQuery, negativeQuery, 0.01f);
        TopDocs hits = searcher.search(query, 100);
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("title"));
        }
    }
}
