package com.kevin.chap8.highlighter;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.CommonTermsQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 类名: HighlightTest<br/>
 * 包名：com.kevin.chap8.highlighter<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/28 15:35<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class HighlightTest extends TestCase {

    private final int QUERY = 0;
    private final int QUERY_TERM = 1;
    private final String FIELD_NAME = "contents";
    private final String NUMERIC_FIELD_NAME = "nfield";
    private final Analyzer analyzer = new StandardAnalyzer();
    private Directory dir = new RAMDirectory();
    private IndexReader reader;
    private IndexSearcher searcher;
    private int numHighlights = 0;
    private TopDocs hits;
    private int mode = QUERY;
    private Fragmenter fragmenter = new SimpleFragmenter(20);

    private FieldType FIELD_TYPE_TV;

    {
        FieldType fieldType = new FieldType();
        fieldType.setTokenized(true);
        fieldType.setStored(true);
        fieldType.setStoreTermVectors(true);
        fieldType.setStoreTermVectorPositions(true);
        fieldType.setStoreTermVectorOffsets(true);
        fieldType.freeze();
        FIELD_TYPE_TV = fieldType;
    }

    private String[] texts = {
            "Hello this is a piece of text that is very long and contains too much preamble and the meat is really here which says kennedy has been shot",
            "This piece of text refers to Kennedy at the beginning then has a longer piece of text that is very long in the middle and finally ends with another reference to Kennedy",
            "JFK has been shot", "John Kennedy Kennedy has been shot",
            "This text has a typo in referring to Keneddy",
            "wordx wordy wordz wordx wordy wordx worda wordb wordy wordc",
            "y z x y z a b", "lets is a the lets is a the lets is a the lets"
    };

    private void createIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, config);

        // 添加文本域
        for (String text : texts) {
            writer.addDocument(createTextDoc(FIELD_NAME, text));
        }

        // 添加数字域
        writer.addDocument(createIntDoc(NUMERIC_FIELD_NAME, 1));
        writer.addDocument(createIntDoc(NUMERIC_FIELD_NAME, 3));
        writer.addDocument(createIntDoc(NUMERIC_FIELD_NAME, 5));
        writer.addDocument(createIntDoc(NUMERIC_FIELD_NAME, 7));

        Document childDoc = createTextDoc(FIELD_NAME, "child document");
        Document parentDoc = createTextDoc(FIELD_NAME, "parent document");
        writer.addDocument(childDoc);
        writer.addDocument(parentDoc);

        writer.close();
    }

    private Document createTextDoc(String name, String value) {
        Document doc = new Document();
//        doc.add(new Field(name, value, FIELD_TYPE_TV));
        doc.add(new TextField(name, value, Field.Store.YES));
        return doc;
    }

    private Document createIntDoc(String name, int value) {
        Document doc = new Document();
        doc.add(new IntPoint(NUMERIC_FIELD_NAME, 1));
        doc.add(new StoredField(NUMERIC_FIELD_NAME, 1));
        return doc;
    }

    @Override
    public void setUp() throws IOException {
        createIndex();
        reader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(reader);
    }

    @Override
    public void tearDown() throws IOException {
        reader.close();
        dir.close();
    }

    public void testCommonTermQueryHighlight() throws IOException, InvalidTokenOffsetsException {
        CommonTermsQuery query = new CommonTermsQuery(BooleanClause.Occur.MUST,
                BooleanClause.Occur.SHOULD, 3);
        query.add(new Term(FIELD_NAME, "this"));
        query.add(new Term(FIELD_NAME, "long"));
        query.add(new Term(FIELD_NAME, "very"));
        TopDocs hits = searcher.search(query, 10);
        System.out.println("hits.totalHits:" + hits.totalHits);

        QueryScorer scorer = new QueryScorer(query, FIELD_NAME);
        Highlighter highlighter = new Highlighter(scorer);

        Document doc = searcher.doc(hits.scoreDocs[0].doc);
        String storedField = doc.get(FIELD_NAME);
        TokenStream tokenStream = TokenSources.getTokenStream(FIELD_NAME, null, storedField, analyzer, highlighter.getMaxDocCharsToAnalyze() - 1);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
        highlighter.setTextFragmenter(fragmenter);
        String fragment = highlighter.getBestFragment(tokenStream, storedField);
        System.out.println("fragment:" + fragment);


    }
}
