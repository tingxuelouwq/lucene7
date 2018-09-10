package com.kevin.chap8.pinyin;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 类名: PinyinSearchTest<br/>
 * 包名：com.kevin.chap8.pinyin<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/10 10:15<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class PinyinSearchTest {

    public static void main(String[] args) throws IOException {
        String fieldName = "content";
        String queryString = "sunyanzi";
        Directory dir = FSDirectory.open(Paths.get("D:/lucene/index"));
        Analyzer analyzer = new PinyinAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, config);

        /** 创建索引Begin **/
        Document doc1 = new Document();
        doc1.add(new TextField(fieldName, "孙燕姿，新加坡籍华语流行音乐女歌手，刚出道便被誉为华语“四小天后”之一。", Field.Store.YES));
        writer.addDocument(doc1);

        Document doc2 = new Document();
        doc2.add(new TextField(fieldName, "1978年7月23日，孙燕姿出生于新加坡，祖籍中国广东省潮州市，父亲孙耀宏是新加坡南洋理工大学电机系教授，母亲是一名教师。姐姐孙燕嘉比燕姿大三岁，任职新加坡巴克莱投资银行副总裁，妹妹孙燕美小六岁，是新加坡国立大学医学硕士，燕姿作为家中的第二个女儿，次+女=姿，故取名“燕姿”", Field.Store.YES));
        writer.addDocument(doc2);

        Document doc3 = new Document();
        doc3.add(new TextField(fieldName, "孙燕姿毕业于新加坡南洋理工大学，父亲是燕姿音乐的启蒙者，燕姿从小热爱音乐，五岁开始学钢琴，十岁第一次在舞台上唱歌，十八岁写下第一首自己作词作曲的歌《Someone》。", Field.Store.YES));
        writer.addDocument(doc3);

        Document doc4 = new Document();
        doc4.add(new TextField(fieldName, "华纳音乐于2000年6月9日推出孙燕姿的首张音乐专辑《孙燕姿同名专辑》，孙燕姿由此开始了她的音乐之旅。", Field.Store.YES));
        writer.addDocument(doc4);

        Document doc5 = new Document();
        doc5.add(new TextField(fieldName, "2000年，孙燕姿的首张专辑《孙燕姿同名专辑》获得台湾地区年度专辑销售冠军，在台湾卖出30余万张的好成绩，同年底，发行第二张专辑《我要的幸福》", Field.Store.YES));
        writer.addDocument(doc5);

        Document doc6 = new Document();
        doc6.add(new TextField(fieldName, "2011年3月31日，孙燕姿与相恋5年多的男友纳迪姆在新加坡登记结婚", Field.Store.YES));
        writer.addDocument(doc6);

        Document doc7 = new Document();
        doc7.add(new TextField(fieldName, "游侠汽车已陆续完成A轮、B轮以及B+轮共三轮融资，累计融资规模超过12.5亿美金，整体估值达到33.5亿美金", Field.Store.YES));
        writer.addDocument(doc7);

        Document doc8 = new Document();
        doc8.add(new TextField(fieldName, " 5月16日，Jingdate联合36kr权威发布2018中国汽车交通产业价值榜单，游侠汽车获最具投资潜力奖， 目前国内新造车企综合价值实力排名第四", Field.Store.YES));
        writer.addDocument(doc8);

        // 强制合并为1个段
        writer.forceMerge(1);
        writer.close();
        /** 创建索引End **/

        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = new TermQuery(new Term(fieldName, queryString));
        TopDocs hits = searcher.search(query, 100);
        ScoreDoc[] docs = hits.scoreDocs;
        if(null == docs || docs.length <= 0) {
            System.out.println("No results.");
            return;
        }
        System.out.println("ID[Score]\tcontent");
        for (ScoreDoc scoreDoc : docs) {
            int docID = scoreDoc.doc;
            Document document = searcher.doc(docID);
            String content = document.get(fieldName);
            float score = scoreDoc.score;
            System.out.println(docID + "[" + score + "]\t" + content);
        }
    }
}
