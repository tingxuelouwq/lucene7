package com.kevin.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.io.StringReader;

/**
 * @类名: AnalyzerUtils
 * @包名：com.kevin.util
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/10 16:33
 * @版本：1.0
 * @描述：
 */
public class AnalyzerUtils {

    public static void displayTokens(Analyzer analyzer, String text) throws IOException {
        displayTokens(analyzer.tokenStream("contents", new StringReader(text)));
    }

    private static void displayTokens(TokenStream tokenStream) throws IOException {
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            System.out.print("[" + charTermAttribute.toString() + "] ");
        }
        tokenStream.end();
        tokenStream.close();
    }

    public static void displayTokensWithFullDetails(Analyzer analyzer, String text) throws IOException {
        TokenStream tokenStream = analyzer.tokenStream("contents", new StringReader(text));
        CharTermAttribute charTermAttribute =
                tokenStream.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute positionIncrementAttribute =
                tokenStream.addAttribute(PositionIncrementAttribute.class);
        OffsetAttribute offsetAttribute =
                tokenStream.addAttribute(OffsetAttribute.class);
        TypeAttribute typeAttribute =
                tokenStream.addAttribute(TypeAttribute.class);
        tokenStream.reset();
        int position = 0;
        while (tokenStream.incrementToken()) {
            int increment = positionIncrementAttribute.getPositionIncrement();
            if (increment > 0) {
                position = position + increment;
                System.out.print(position + ": ");
            }
            System.out.println("[" +
                    charTermAttribute.toString() + ":" +
                    offsetAttribute.startOffset() + "->" +
                    offsetAttribute.endOffset() + ":" +
                    typeAttribute.type() + "]");
        }
        tokenStream.end();
        tokenStream.close();
    }

    public static void displayTokensWithPosition(Analyzer analyzer, String text) throws IOException {
        TokenStream tokenStream = analyzer.tokenStream("contents", new StringReader(text));
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute positionIncrementAttribute = tokenStream.addAttribute(PositionIncrementAttribute.class);
        tokenStream.reset();
        int position = 0;
        while (tokenStream.incrementToken()) {
            int increment = positionIncrementAttribute.getPositionIncrement();
            if (increment > 0) {
                position = position + increment;
                System.out.println();
                System.out.print(position + ": ");
            }
            System.out.print("[" + charTermAttribute.toString() + "] ");
        }
        System.out.println();
        tokenStream.end();
        tokenStream.close();
    }

    public static void main(String[] args) {
        String str = "hello, i'm a boy, and i like play basketball" ;
        String ztr = "你好，我是一个男孩，我喜欢打篮球" ;
        Analyzer a = new StandardAnalyzer() ;      //标准分词器
        Analyzer b = new SimpleAnalyzer() ;        //简单分词器
        Analyzer c = new StopAnalyzer() ;          //停用词分词器
        Analyzer d = new WhitespaceAnalyzer() ; //空格分词器
        Analyzer analyzer = new CJKAnalyzer() ; //中文分词器
        display(str,a) ;
        System. out.println( "-----------------------------");
        display(str,b) ;
        System. out.println( "-----------------------------");
        display(str,c) ;
        System. out.println( "-----------------------------");
        display(str,d) ;
        System. out.println( "-----------------------------");
        display(ztr,analyzer) ;
    }

    public static void display(String str, Analyzer a) {
        TokenStream stream = null ;
        try {
            stream = a.tokenStream( "renyi", new StringReader(str)) ;
            PositionIncrementAttribute pia = stream.addAttribute(PositionIncrementAttribute.class ) ;  //保存位置
            OffsetAttribute oa = stream.addAttribute(OffsetAttribute.class ) ; //保存辞与词之间偏移量
            CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class ) ;//保存响应词汇
            TypeAttribute ta = stream.addAttribute(TypeAttribute.class ) ; //保存类型
            //在lucene 4 以上  要加入reset 和  end方法
            stream.reset() ;
            while (stream.incrementToken()) {
                System. out.println(pia.getPositionIncrement() + ":[" + cta.toString() + "]:" + oa.startOffset() + "->" + oa.endOffset() + ":" + ta.type());
            }
            stream.end() ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
