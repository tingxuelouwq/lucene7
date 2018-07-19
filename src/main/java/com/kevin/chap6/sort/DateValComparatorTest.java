package com.kevin.chap6.sort;

import com.kevin.util.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

/**
 * @类名: DateValComparatorTest
 * @包名：com.kevin.chap6.sort
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/19 15:44
 * @版本：1.0
 * @描述：
 */
public class DateValComparatorTest extends TestCase {

    private Directory directory;
    private IndexReader reader;
    private IndexSearcher searcher;

    @Override
    public void setUp() throws IOException {
        directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter writer = new IndexWriter(directory, config);
        addDocument(writer, "A Modern Art of Education", "2004-03-01");
        addDocument(writer, "Imperial Secrets of Health and Longevity", "1999-03-12");
        addDocument(writer, "Tao Te Ching 道德經", "2006-09-11");
        addDocument(writer, "Ant in Action", "2007-07-09");
        addDocument(writer, "JUnit in Action, Second Edition", "2010-05-05");
        writer.close();

        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    @Override
    public void tearDown() throws IOException {
        reader.close();
        directory.close();
    }

    private void addDocument(IndexWriter writer, String title, String pubmonth) throws IOException {
        Document document = new Document();
        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new BinaryDocValuesField("pubmonth", new BytesRef(pubmonth)));
        document.add(new StoredField("pubmonth", pubmonth));
        writer.addDocument(document);
    }

    public void testDateValComprator() throws IOException {
        TermRangeQuery query = TermRangeQuery.newStringRange("pubmonth",
                "2004-01-01", "2010-01-01", true, true);
        Sort sort = new Sort(new SortField("pubmonth", new DateValComparatorSource(), true));
        TopDocs topDocs = searcher.search(query, 10, sort);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("title"));
        }
    }
}
