package com.kevin.chap4;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

/**
 * @类名: TermFreqTest
 * @包名：com.kevin.other
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/17 9:22
 * @版本：1.0
 * @描述：
 */
public class TermFreqTest extends TestCase {

    /**
     * 获取分词后term的位置信息
     * @throws IOException
     */
    public void testPosition() throws IOException {
        String word = "中新网3月12日电 据中国政府网消息，3月12日上午10时15分，" +
                "李克强总理参加完政协闭幕会后来到国务院应急指挥中心，与前方中国搜救" +
                "船长通话，了解马航MH370失联客机搜救最新进展情况。李克强要求各有关部门" +
                "调集一切可能力量，加大搜救密度和力度，不放弃任何一线希望。";
        Analyzer analyzer = new IKAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("a", new StringReader(word));
        tokenStream.reset();
        CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class); // term信息
        OffsetAttribute offset = tokenStream.addAttribute(OffsetAttribute.class);   // 位置信息
        while (tokenStream.incrementToken()) {
            System.out.println("(" + term + "," + offset.startOffset() + "," +
                    offset.endOffset() + ")");
        }
        tokenStream.end();
        tokenStream.close();
    }

    public void testStem() throws IOException {
        String word = "happiness rotting rolling";
        Analyzer analyzer = new EnglishAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("stem", new StringReader(word));
        tokenStream.reset();
        CharTermAttribute termAttr = tokenStream.addAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAttr = tokenStream.addAttribute(OffsetAttribute.class);
        while (tokenStream.incrementToken()) {
            System.out.println("(" + termAttr + "," + offsetAttr.startOffset() + "," +
                    offsetAttr.endOffset() + ")");
        }
        tokenStream.end();
        tokenStream.close();
    }

    /**
     * 获取索引中的词频信息
     */
    public void testGetTFAndIDF() throws IOException {
        String text1 = "教育技术作为教育活动的一个重要方面可谓源远流长，但那主要是它所涉及的教学媒体而言的";
        String text2 = "教育技术学是一门以教育技术为研究对象、形成与发展以及类型的学科";

        Directory dir = FSDirectory.open(Paths.get("D:/lucene/index"));
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, config);
        FieldType type = new FieldType();
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        type.setTokenized(true);
        type.setStored(true);
        type.setStoreTermVectors(true);
        type.setStoreTermVectorPositions(true);
        type.setStoreTermVectorOffsets(true);
        Document doc1 = new Document();
        doc1.add(new Field("content", text1, type));
        writer.addDocument(doc1);
        Document doc2 = new Document();
        doc2.add(new Field("content", text2, type));
        writer.addDocument(doc2);
        writer.close();

        IndexReader reader = DirectoryReader.open(dir);
        for (int i = 0; i < reader.numDocs(); i++) {
            System.out.println("第" + (i + 1) + "篇文档: ");
            Terms terms = reader.getTermVector(i, "content");
            if (terms == null) {
                continue;
            }
            TermsEnum termsEnum = terms.iterator();
            BytesRef thisTerm = null;
            while ((thisTerm = termsEnum.next()) != null) {
                String termText = thisTerm.utf8ToString();  // term
                int docFreq = termsEnum.docFreq();          // tf
                long termFreq = termsEnum.totalTermFreq();  // idf
                System.out.println("term: " + termText + ", tf: " + termFreq + ", idf: " + docFreq);
            }
        }
        reader.close();
        dir.close();
    }
}
