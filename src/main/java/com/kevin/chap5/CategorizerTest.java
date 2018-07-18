package com.kevin.chap5;

import com.kevin.util.JsonUtil;
import com.kevin.util.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @类名: CategorizerTest
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/18 10:07
 * @版本：1.0
 * @描述：
 */
public class CategorizerTest extends TestCase {

    private Map<String, Map<String, Long>> categoryMap;

    @Override
    public void setUp() throws IOException {
        categoryMap = new TreeMap<>();
        buildCategoryVectors();
        dumpCategoryVectors();
    }

    /**
     * 通过聚合各个类别来建立类别向量
     * @throws IOException
     */
    private void buildCategoryVectors() throws IOException {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        int numDocs = reader.numDocs();
        for (int i = 0; i < numDocs; i++) {
            Document doc = reader.document(i);
            String category = doc.get("category");
            Map<String, Long> vectorMap = categoryMap.get(category);
            if (vectorMap == null) {
                vectorMap = new TreeMap<>();
                categoryMap.put(category, vectorMap);
            }
            Terms terms = reader.getTermVector(i, "subject");
            addTermFreqToMap(vectorMap, terms);
        }
    }

    /**
     * 类别向量中的value也是一个Map，其中key为term，value为term出现的频率
     * @param vectorMap
     * @param terms
     * @throws IOException
     */
    private void addTermFreqToMap(Map<String, Long> vectorMap, Terms terms)
            throws IOException {
        TermsEnum termsEnum = terms.iterator();
        BytesRef thisTerm;
        while ((thisTerm = termsEnum.next()) != null) {
            String term = thisTerm.utf8ToString();
            if (vectorMap.containsKey(term)) {
                Long value = vectorMap.get(term);
                vectorMap.put(term, value + termsEnum.totalTermFreq());
            } else {
                vectorMap.put(term, termsEnum.totalTermFreq());
            }
        }
    }

    /**
     * 计算主题关键字与各个类别之间的相似度，并返回相似度最高的类别
     * @param subject
     * @return
     */
    private String getCategory(String subject) {
        String[] words = subject.split(" ");
        Iterator<String> categoryIterator = categoryMap.keySet().iterator();
        double bestAngle = Double.MAX_VALUE;
        String bestCategory = null;
        while (categoryIterator.hasNext()) {
            String category = categoryIterator.next();
            double angle = computeAngle(words, category);
            if (angle < bestAngle) {
                bestAngle = angle;
                bestCategory = category;
            }
        }
        return bestCategory;
    }

    /**
     * 计算主题关键字与某个类别之间的相似度
     * @param words
     * @param category
     * @return
     */
    private double computeAngle(String[] words, String category) {
        Map<String, Long> vectorMap = categoryMap.get(category);
        int dotProduct = 0;
        int sumOfSquars = 0;
        for (String word : words) {
            long categoryWordFreq = 0;
            if (vectorMap.containsKey(word)) {
                categoryWordFreq = vectorMap.get(word);
            }
            dotProduct += categoryWordFreq; /* 这里假设每个单词在单词数组里出现的频率为1。
                                             * 点积的计算公式为：a·b=(a1,a2,...,am)·(b1,b2,...,bn)=a1*b1+a2*b2+...，即
                                             * 每一项乘积之和。由于我们假设每个单词在单词数组里出现的频率为1，所以a1=a2=...
                                             * =am=1，而bi则是categoryWordFreq，因此dotProduct=1*categoryWordFreq+
                                             * 1*categoryWordFreq+...=sum(categoryWordFreq)
                                             */
            sumOfSquars += categoryWordFreq * categoryWordFreq; /* 分母的值为：|a|*|b|。由于我们假设每个单词在单词数组
                                                                 * 里出现的频率为1，所以|a|=sqrt(words.length)，而|b|=
                                                                 * sqrt(b1^2+b2^2+...)
                                                                 */
        }
        double denominator;
        if (sumOfSquars == words.length) {  /* 避免比值可能大于1的情况，从而解决精度问题。例如分子为3，分母为
                                             * sqrt(3)*sqrt(3)=2.9999999999999996，则比值大于1，这是余弦函数不允许的。
                                             */
            denominator = sumOfSquars;
        } else {
            denominator = Math.sqrt(sumOfSquars) * Math.sqrt(words.length);
        }
        double ratio = dotProduct / denominator;
        return Math.acos(ratio);    // 根据余弦值求角度，例如cos60度=1/2，则arrcos1/2=60度
    }

    private void dumpCategoryVectors() {
        System.out.println(JsonUtil.bean2Json(categoryMap));
    }

    public void testCategorization() {
        System.out.println(getCategory("extreme agile methodology"));
        System.out.println(getCategory("montessori education philosophy"));
    }

    public void testTermEnums() throws IOException {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        int numDocs = reader.numDocs();
        for (int i = 0; i < numDocs; i++) {
            Document doc = reader.document(i);
            System.out.println(doc.get("title"));
            Terms terms = reader.getTermVector(i, "subject");
            TermsEnum termsEnum = terms.iterator();
            BytesRef thisTerm;
            while ((thisTerm = termsEnum.next()) != null) {
                String term = thisTerm.utf8ToString();
                int idf = termsEnum.docFreq();
                long tf = termsEnum.totalTermFreq();
                System.out.println("(" + term + "," + idf + "," + tf + ")");
            }
            System.out.println("---------");
        }
    }
}
