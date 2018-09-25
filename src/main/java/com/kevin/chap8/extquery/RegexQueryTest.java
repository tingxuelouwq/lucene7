package com.kevin.chap8.extquery;

import com.kevin.util.TestUtil;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * 类名: RegexQueryTest<br/>
 * 包名：com.kevin.chap8.ext<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/28 15:23<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class RegexQueryTest {

    public static void main(String[] args) throws IOException {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        RegexpQuery query = new RegexpQuery(new Term("title", ".*st.*"));
        TopDocs hits = searcher.search(query, 10);
        TestUtil.dumpHits(searcher, hits);
    }
}
