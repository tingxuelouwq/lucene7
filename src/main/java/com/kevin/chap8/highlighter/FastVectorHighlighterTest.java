package com.kevin.chap8.highlighter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.vectorhighlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * 类名: FastVectorHighlighterTest<br/>
 * 包名：com.kevin.chap8.highlighter<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/27 9:20<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class FastVectorHighlighterTest {

    private static final String[] DOCS = {
        "the quick brown fox jumps over the lazy dog",
        "the quick gold fox jumped over the lazy black dog",
        "the quick fox jumps over the black dog",
        "the red fox jumped over the lazy dark gray dog"
    };

    private static final String QUERY = "quick OR fox OR \"lazy dog\"~1";
    private static final String F = "f";
    private static final Directory dir = new RAMDirectory();
    private static final Analyzer analyzer = new StandardAnalyzer();

    public static void main(String[] args) throws IOException, ParseException {
        makeIndex();
        searchIndex();
    }

    private static void makeIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(dir, config);
        for (String d : DOCS) {
            Document doc = new Document();
            FieldType fieldType = new FieldType();
            fieldType.setTokenized(true);
            fieldType.setStored(true);
            fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            fieldType.setStoreTermVectors(true);
            fieldType.setStoreTermVectorPositions(true);
            fieldType.setStoreTermVectorOffsets(true);
            doc.add(new Field(F, d, fieldType));
            writer.addDocument(doc);
        }
        writer.close();
    }

    private static void searchIndex() throws ParseException, IOException {
        QueryParser parser = new QueryParser(F, analyzer);
        Query query = parser.parse(QUERY);
        FastVectorHighlighter highlighter = getHighlighter();
        FieldQuery fieldQuery = highlighter.getFieldQuery(query);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(query, 10);

        StringBuilder builder = new StringBuilder();
        builder.append("<html>").append("<body>")
                .append("<p>QUERY: ").append(QUERY).append("</p>");
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            String snippet = highlighter.getBestFragment(fieldQuery, reader,
                    scoreDoc.doc, F, 100);
            if (snippet != null) {
                builder.append(scoreDoc.doc).append(": ").append(snippet).append("<br/>");
            }
        }
        builder.append("</body></html>");
        System.out.println(builder.toString());
        reader.close();
    }

    private static FastVectorHighlighter getHighlighter() {
        FragListBuilder fragListBuilder = new SimpleFragListBuilder();
        FragmentsBuilder fragmentsBuilder = new ScoreOrderFragmentsBuilder(
                BaseFragmentsBuilder.COLORED_PRE_TAGS,
                BaseFragmentsBuilder.COLORED_POST_TAGS
        );
        return new FastVectorHighlighter(true, true,
                fragListBuilder, fragmentsBuilder);
    }
}
