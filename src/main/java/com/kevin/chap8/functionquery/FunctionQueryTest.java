package com.kevin.chap8.functionquery;

import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类名: FunctionQueryTest<br/>
 * 包名：com.kevin.chap8.functionquery<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/21 11:10<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class FunctionQueryTest {

    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static Directory dir;

    public static void main(String[] args) throws IOException, ParseException {
        createIndex();
        try (IndexReader reader = DirectoryReader.open(dir)) {
            long now = format.parse("2015-04-13").getTime();
            IndexSearcher searcher = new IndexSearcher(reader);
            TermQuery termQuery = new TermQuery(new Term("title", "solr"));
            FunctionQuery functionQuery = new FunctionQuery(
                    new DateDampingValueScore("publishDate", now));
            CustomScoreQuery query = new CustomScoreQuery(termQuery, functionQuery);
            Sort sort = new Sort(new SortField[]{SortField.FIELD_SCORE});
            TopDocs topDocs = searcher.search(query, 10, sort, true, false);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                int docID = scoreDoc.doc;
                Document document = searcher.doc(docID);
                String title = document.get("title");
                String publishDateString = document.get("publishDate");
                long publishMills = Long.valueOf(publishDateString);
                Date date = new Date(publishMills);
                publishDateString = format.format(date);
                float score = scoreDoc.score;
                System.out.println(docID + ", " + title + ", " + publishDateString + ", " + score);
            }
        }
    }

    private static void createIndex() throws IOException, ParseException {
        dir = FSDirectory.open(Paths.get("D:/lucene/index"));
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        try (IndexWriter writer = new IndexWriter(dir, config)) {
            Document doc1 = createDocument("Lucene in action 2th edition", "2010-05-05");
            Document doc2 = createDocument("Lucene Progamming", "2008-07-11");
            Document doc3 = createDocument("Lucene User Guide", "2014-11-24");
            Document doc4 = createDocument("Lucene5 Cookbook", "2015-01-09");
            Document doc5 = createDocument("Apache Lucene API 5.0.0", "2015-02-25");
            Document doc6 = createDocument("Apache Solr 4 Cookbook", "2013-10-22");
            Document doc7 = createDocument("Administrating Solr", "2015-01-20");
            Document doc8 = createDocument("Apache Solr Essentials", "2013-08-16");
            Document doc9 = createDocument("Apache Solr High Performance", "2014-06-28");
            Document doc10 = createDocument("Apache Solr API 5.0.0", "2015-03-02");
            writer.addDocument(doc1);
            writer.addDocument(doc2);
            writer.addDocument(doc3);
            writer.addDocument(doc4);
            writer.addDocument(doc5);
            writer.addDocument(doc6);
            writer.addDocument(doc7);
            writer.addDocument(doc8);
            writer.addDocument(doc9);
            writer.addDocument(doc10);
        }
    }

    private static Document createDocument(String title, String publishDateStr) throws ParseException {
        Date publishDate = format.parse(publishDateStr);
        Document doc = new Document();
        doc.add(new TextField("title",title,Field.Store.YES));
        doc.add(new LongPoint("publishDate", publishDate.getTime()));
        doc.add(new StoredField("publishDate", publishDate.getTime()));
        doc.add(new NumericDocValuesField("publishDate", publishDate.getTime()));
        return doc;
    }
}
