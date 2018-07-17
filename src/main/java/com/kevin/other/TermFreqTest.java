package com.kevin.other;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;

/**
 * @类名: TermFreqTest
 * @包名：com.kevin.other
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/17 9:22
 * @版本：1.0
 * @描述：
 */
public class TermFreqTest {

    public static void main(String[] args) throws IOException {
        TermFreqTest termFreqTest = new TermFreqTest();
        String word = "中新网3月12日电 据中国政府网消息，3月12日上午10时15分，" +
                "李克强总理参加完政协闭幕会后来到国务院应急指挥中心，与前方中国搜救" +
                "船长通话，了解马航MH370失联客机搜救最新进展情况。李克强要求各有关部门" +
                "调集一切可能力量，加大搜救密度和力度，不放弃任何一线希望。";
//        termFreqTest.position(word);
        termFreqTest.getTFAndIDF(word);
    }

    /**
     * 获取分词后term的位置信息
     * @param word
     * @throws IOException
     */
    public void position(String word) throws IOException {
        Analyzer analyzer = new IKAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("a", new StringReader(word));
        tokenStream.reset();
        CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class); // term信息
        OffsetAttribute offset = tokenStream.addAttribute(OffsetAttribute.class);   // 位置信息
        while (tokenStream.incrementToken()) {
            System.out.println(term + " " + offset.startOffset() + " " + offset.endOffset());
        }
        tokenStream.end();
        tokenStream.close();
    }

    /**
     * 获取索引中的词频信息
     */
    public void getTFAndIDF(String word) throws IOException {
        FieldType type = new FieldType();
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        type.setTokenized(true);
        type.setStored(true);
        type.setStoreTermVectors(true);
        type.setStoreTermVectorPositions(true);
        type.setStoreTermVectorOffsets(true);
        Document doc = new Document();
        doc.add(new Field("name", word, type));

        Directory dir = new RAMDirectory();
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(dir, config);
        writer.addDocument(doc);
        writer.close();

        IndexReader reader = DirectoryReader.open(dir);
        for (int i = 0; i < reader.numDocs(); i++) {
            System.out.println("第" + (i + 1) + "篇文档: ");
            Terms terms = reader.getTermVector(i, "name");
            if (terms == null) {
                continue;
            }
            TermsEnum termsEnum = terms.iterator();
            BytesRef thisTerm = null;
            while ((thisTerm = termsEnum.next()) != null) {
                String termText = thisTerm.utf8ToString();  // term
                int docFreq = termsEnum.docFreq();          // idf
                long termFreq = termsEnum.totalTermFreq();  // tf
                System.out.println("term: " + termText + ", idf: " + docFreq + ", tf: " + termFreq);
            }
        }
        reader.close();
        dir.close();
    }
}
