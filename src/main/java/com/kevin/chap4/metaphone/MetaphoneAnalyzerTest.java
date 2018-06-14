package com.kevin.chap4.metaphone;

import com.kevin.util.AnalyzerUtils;
import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * @类名: MetaphoneAnalyzerTEST
 * @包名：com.kevin.chap4
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/12 9:59
 * @版本：1.0
 * @描述：
 */
public class MetaphoneAnalyzerTest extends TestCase {

    public void testKoolKat() throws IOException, ParseException {
        Directory directory = new RAMDirectory();
        Analyzer analyzer = new MetaphoneReplacementAnalyzer();
        IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer));
        Document document = new Document();
        document.add(new TextField("contents", "cool cat", Field.Store.YES));
        writer.addDocument(document);
        writer.close();

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser("contents", analyzer);
        Query query = parser.parse("kool kat");
        TopDocs topDocs = searcher.search(query, 1);
        System.out.println(topDocs.totalHits);  // 1
        int docId = topDocs.scoreDocs[0].doc;
        document = searcher.doc(docId);
        System.out.println(document.get("contents"));
        reader.close();
    }

    public static void main(String[] args) throws IOException {
        MetaphoneReplacementAnalyzer analyzer =
                new MetaphoneReplacementAnalyzer();
        AnalyzerUtils.displayTokensWithPosition(analyzer,
                "The quick brown fox jumped over the lazy dog");
        System.out.println("");
        AnalyzerUtils.displayTokensWithPosition(analyzer,
                "Tha quik brown phox jumpd ovvar tha lazi dag");
    }
}
