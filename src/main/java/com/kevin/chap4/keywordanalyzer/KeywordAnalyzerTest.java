package com.kevin.chap4.keywordanalyzer;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @类名: KeywordAnalyzerTest
 * @包名：com.kevin.chap4.perfieldanalyzer
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/13 22:56
 * @版本：1.0
 * @描述：
 */
public class KeywordAnalyzerTest extends TestCase {

    private Directory directory;
    private IndexReader reader;
    private IndexSearcher searcher;

    @Override
    public void setUp() throws IOException {
        directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());
        IndexWriter writer = new IndexWriter(directory, config);
        Document doc = new Document();
        doc.add(new StringField("partnum", "Q36", Field.Store.NO));
        doc.add(new TextField("description", "Illidium Space Modulator", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    @Override
    public void tearDown() throws IOException {
        reader.close();
        directory.close();
    }

    public void testTermQuery() throws IOException {
        TermQuery query = new TermQuery(new Term("partnum", "Q36"));
        System.out.println(searcher.search(query, 1).totalHits);    // 1
    }

    public void testQueryParser() throws IOException, ParseException {
        QueryParser parser = new QueryParser("description", new SimpleAnalyzer());
        Query query = parser.parse("partnum:Q36 AND SPACE");
        System.out.println(query.toString());   // +partnum:q +description:space
        System.out.println(searcher.search(query, 10).totalHits);   // 0
    }

    public void testPerFieldAnalyzerWrapper() throws IOException, ParseException {
        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        analyzerPerField.put("partnum", new KeywordAnalyzer());
        PerFieldAnalyzerWrapper analyzerWrapper =
                new PerFieldAnalyzerWrapper(new SimpleAnalyzer(), analyzerPerField);
        QueryParser parser = new QueryParser("description", analyzerWrapper);
        Query query = parser.parse("partnum:Q36 AND SPACE");
        System.out.println(query.toString());   // +partnum:Q36 +description:space
        System.out.println(searcher.search(query, 10).totalHits);   // 1
    }
}
