package com.kevin.chap5;

import junit.framework.TestCase;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

/**
 * @类名: MultiSearcherTest
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/13 19:15
 * @版本：1.0
 * @描述：
 */
public class MultiSearcherTest extends TestCase {

    private Directory aTOmDirectory;
    private Directory nTOzDirectory;
    private MultiReader multiReader;
    private IndexSearcher searcher;

    @Override
    public void setUp() throws IOException {
        String[] animals = { "aardvark", "beaver", "coati",
                "dog", "elephant", "frog", "gila monster",
                "horse", "iguana", "javelina", "kangaroo",
                "lemur", "moose", "nematode", "orca",
                "python", "quokka", "rat", "scorpion",
                "tarantula", "uromastyx", "vicuna",
                "walrus", "xiphias", "yak", "zebra"};
        aTOmDirectory = new RAMDirectory();
        nTOzDirectory = new RAMDirectory();
        IndexWriterConfig aTOmConfig = new IndexWriterConfig(new WhitespaceAnalyzer());
        IndexWriterConfig nTOzConfig = new IndexWriterConfig(new WhitespaceAnalyzer());
        IndexWriter aTOmWriter = new IndexWriter(aTOmDirectory, aTOmConfig);
        IndexWriter nTOzWriter = new IndexWriter(nTOzDirectory, nTOzConfig);
        for (int i = animals.length - 1; i >= 0; i--) {
            Document doc = new Document();
            String animal = animals[i];
            doc.add(new StringField("animal", animal, Field.Store.YES));
            doc.add(new SortedDocValuesField("animal", new BytesRef(animal)));
            if (animal.charAt(0) < 'n') {
                aTOmWriter.addDocument(doc);
            } else {
                nTOzWriter.addDocument(doc);
            }
        }
        aTOmWriter.close();
        nTOzWriter.close();

        IndexReader aTOmReader = DirectoryReader.open(aTOmDirectory);
        IndexReader nTOzReader = DirectoryReader.open(nTOzDirectory);
        multiReader = new MultiReader(aTOmReader, nTOzReader);
        searcher = new IndexSearcher(multiReader);
    }

    @Override
    public void tearDown() throws IOException {
        multiReader.close();
        aTOmDirectory.close();
        nTOzDirectory.close();
    }

    public void testMultiSearch() throws IOException {
        TermRangeQuery termRangeQuery = new TermRangeQuery("animal", new BytesRef("h"),
                new BytesRef("t"), true, true);
        TopDocs hits = searcher.search(termRangeQuery, 100,
                new Sort(new SortField("animal", SortField.Type.STRING)));
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc).get("animal"));
        }
    }
}
