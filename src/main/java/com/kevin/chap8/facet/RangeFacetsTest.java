package com.kevin.chap8.facet;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.facet.*;
import org.apache.lucene.facet.range.LongRange;
import org.apache.lucene.facet.range.LongRangeFacetCounts;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 类名: RangeFacetsTest<br/>
 * 包名：com.kevin.chap8.facet<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/17 16:49<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class RangeFacetsTest {

    private String fieldName = "timestamp";
    private Directory indexDir = FSDirectory.open(Paths.get("D:/lucene/index"));
    private IndexReader reader;
    private IndexSearcher searcher;

    // 当前毫秒数
    private long nowSec = System.currentTimeMillis();
    // 1小时之前的毫秒数
    private LongRange PAST_HOUR = new LongRange("Past hour", nowSec - 3600L,
            true, nowSec, true);
    // 6小时之前的毫秒数
    private LongRange PAST_SIX_HOURS = new LongRange("Past six hour", nowSec - 21600L,
            true, nowSec, true);
    // 24小时之前的毫秒数
    private LongRange PAST_DAY = new LongRange("Past day", nowSec - 86400L,
            true, nowSec, true);

    public RangeFacetsTest() throws IOException {
    }

    /**
     * 创建测试索引
     * @throws IOException
     */
    public void index() throws IOException {
        IndexWriter indexWriter = new IndexWriter(indexDir,
                new IndexWriterConfig(new WhitespaceAnalyzer())
                        .setOpenMode(IndexWriterConfig.OpenMode.CREATE));
        // 每次按[1000*i]这个斜率递减创建一个索引
        for (int i = 0; i < 100; i++) {
            Document doc = new Document();
            long then = nowSec - i * 1000;
            doc.add(new LongPoint(fieldName, then));
            doc.add(new StoredField(fieldName, then));
            doc.add(new NumericDocValuesField(fieldName, then));
            indexWriter.addDocument(doc);
        }
        indexWriter.close();

        reader = DirectoryReader.open(indexDir);
        searcher = new IndexSearcher(reader);
    }

    public FacetResult search() throws IOException {
        FacetsCollector fc = new FacetsCollector();
        FacetsCollector.search(searcher, new MatchAllDocsQuery(), 10, fc);
        Facets facets = new LongRangeFacetCounts(fieldName, fc,
                new LongRange[]{PAST_HOUR, PAST_SIX_HOURS, PAST_DAY});
        return facets.getTopChildren(10, fieldName, new String[0]);
    }

    public FacetResult drillDown(LongRange range) throws IOException {
        DrillDownQuery q = new DrillDownQuery(new FacetsConfig());
        q.add(fieldName, LongPoint.newRangeQuery(fieldName, range.min, range.max));
        FacetsCollector fc = new FacetsCollector();
        FacetsCollector.search(searcher, q, 10, fc);
        Facets facets = new LongRangeFacetCounts(fieldName, fc, range);
        return facets.getTopChildren(10, fieldName, new String[0]);
    }

    public void close() throws IOException {
        reader.close();
        indexDir.close();
    }

    public static void main(String[] args) throws IOException {
        RangeFacetsTest test = new RangeFacetsTest();
        test.index();

        System.out.println("Facet counting example:");
        System.out.println("-----------------------");
        System.out.println(test.search());

        System.out.println("\n");
        System.out
                .println("Facet drill-down example (timestamp/Past six hours):");
        System.out.println("---------------------------------------------");
        System.out.println(test.drillDown(test.PAST_SIX_HOURS));

        test.close();
    }
}
