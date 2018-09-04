package com.kevin.chap8.suggest;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.*;
import java.util.*;

/**
 * 类名: SuggestTest<br/>
 * 包名：com.kevin.chap8.suggest<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/30 10:57<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class SuggestTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Directory dir = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        AnalyzingInfixSuggester suggester = new AnalyzingInfixSuggester(dir, analyzer);
        // 创建测试数据
        List<Product> products = new ArrayList<>();
        products.add(new Product("Electric Guitar",
                "http://images.example/electric-guitar.jpg", new String[] {
                "US", "CA" }, 100));
        products.add(new Product("Electric Train",
                "http://images.example/train.jpg", new String[] { "US",
                "CA" }, 100));
        products.add(new Product("Acoustic Guitar",
                "http://images.example/acoustic-guitar.jpg", new String[] {
                "US", "ZA" }, 80));
        products.add(new Product("Guarana Soda",
                "http://images.example/soda.jpg",
                new String[] { "ZA", "IE" }, 130));
        // 创建索引
        suggester.build(new ProductIterator(products.iterator()));
        // 开始搜索
        lookup(suggester, "Gu", "US");
        lookup(suggester, "Gu", "ZA");
        lookup(suggester, "Gui", "CA");
        lookup(suggester, "Electric guit", "US");
    }

    private static void lookup(AnalyzingInfixSuggester suggester, String name, String region)
            throws IOException, ClassNotFoundException {
        Set<BytesRef> contexts = new HashSet<>();
        contexts.add(new BytesRef(region.getBytes("UTF8")));
        List<Lookup.LookupResult> results = suggester.lookup(name, contexts, 2,
                true, true);
        System.out.println("-- \"" + name + "\" (" + region + "):");
        for (Lookup.LookupResult result : results) {
            System.out.println(result.key);
            System.out.println(result.highlightKey.toString());
            // 从payload中反序列化出Product对象
            BytesRef bytesRef = result.payload;
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytesRef.bytes));
            Product product = (Product) in.readObject();
            System.out.println("product-Name:" + product.getName());
            System.out.println("product-regions:" + Arrays.toString(product.getRegions()));
            System.out.println("product-image:" + product.getImage());
            System.out.println("product-numberSold:" + product.getNumberSold());
        }
        System.out.println();
    }
}
