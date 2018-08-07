package com.kevin.chap8.grouping;

import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.grouping.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

/**
 * @类名: GroupTest
 * @包名：com.kevin.chap8.grouping
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/8/6 17:09
 * @版本：1.0
 * @描述：
 */
public class GroupTest {

    private static Directory directory;
    private static final String groupField = "author";

    public static void main(String[] args) throws IOException {
        createIndex();
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = new TermQuery(new Term("content", "random"));
        Sort groupSort = Sort.RELEVANCE;
        groupBy(searcher, query, groupSort);
        groupSearch(searcher);

    }

    private static void groupSearch(IndexSearcher searcher) {
        Sort groupSort = Sort.RELEVANCE;

    }

    private static void groupBy(IndexSearcher searcher, Query query, Sort groupSort) throws IOException {
        /** 前N条中分组 **/
        final int topNGroups = 10;
        /** 分组起始偏移量 **/
        final int groupOffset = 0;
        /** 是否填充SearchGroup的sortValues **/
        final boolean fillFields = true;
        /** 是否缓存评分 **/
        final boolean cacheScores = true;
        /** 第一次查询时缓存大小 **/
        final double maxCacheRAMMB = 16.0;
        /** groupSort用于对组进行排序，docSort用于对组内记录进行排序，多数情况下两者是相同的，但也可不同 **/
        final Sort docSort = groupSort;
        /** 每组返回多少条结果 **/
        final int docsPerGroup = 2;
        /** 是否需要计算总的分组数量 **/
        final boolean requiredTotalGroupCount = true;
        /** 是否获取每个分组内部每个索引的评分 **/
        final boolean getScores = true;
        /** 是否计算最大评分 **/
        final boolean getMaxScores = true;
        /** 用于组内分页，起始偏移量 **/
        final int docOffset = 0;

        /**
         * 将FirstPassGroupingCollector包装成CachingCollector，为第一次查询缓存，避免重复评分。
         * CachingCollector就是用来为结果收集器添加缓存功能的
         */
        FirstPassGroupingCollector<BytesRef> c1 = new FirstPassGroupingCollector<>(new TermGroupSelector(groupField), groupSort, groupOffset + topNGroups);
        CachingCollector cachingCollector = CachingCollector.create(c1, cacheScores, maxCacheRAMMB);
        // 第一次分组统计
        searcher.search(query, cachingCollector);
        /**
         * 第一次查询返回的结果集中只有分组域值groupValue以及每组总的评分sortValues，至于每个分组里有几条以及
         * 分别对应哪些文档，需要第二次查询获取
         */
        Collection<SearchGroup<BytesRef>> topGroups = c1.getTopGroups(groupOffset, fillFields);
        if (topGroups == null) {
            System.out.println("No groups matched");
            return;
        } else {
            for (SearchGroup<BytesRef> searchGroup : topGroups) {
                BytesRef groupValue = searchGroup.groupValue;
                Object[] sortValues = searchGroup.sortValues;
                System.out.println("SearchGroup(groupValue=" + (groupValue == null ? null : groupValue.utf8ToString())+ " sortValues=" + Arrays.toString(sortValues) + ")");
            }
            System.out.println("-----------------------");
        }

        Collector secondPassGroupingCollector = null;
        TopGroupsCollector c2 = new TopGroupsCollector(new TermGroupSelector(groupField), topGroups, groupSort, docSort, docsPerGroup, getScores, getMaxScores, fillFields);
        /**
         * 如果计算总的分组数量，则需要把SecondPassGroupingCollector包装秤
         * T
         */
        AllGroupsCollector c3 = null;
        if (requiredTotalGroupCount) {
            c3 = new AllGroupsCollector(new TermGroupSelector("author"));
            secondPassGroupingCollector = MultiCollector.wrap(c2, c3);
        } else {
            secondPassGroupingCollector = c2;
        }

        /** 如果第一次查询已经加了缓存，则直接从缓存中取 **/
        if (cachingCollector.isCached()) {
            cachingCollector.replay(secondPassGroupingCollector);
        } else {
            // 开始第二次分组查询
            searcher.search(query, secondPassGroupingCollector);
        }

        /** 所有组的数量 **/
        int totalGroupCount = 0;
        /** 所有满足条件的记录数 **/
        int totalHitCount = 0;
        /** 所有组内的满足条件的记录数（通常该值与totalHitCount是一致的） **/
        int totalGroupedHitCount = 0;
        if (requiredTotalGroupCount) {
            totalGroupCount = c3.getGroupCount();
        }

        // 打印总的分组数量
        System.out.println("groupCount: " + totalGroupCount);

        // 打印第一次查询的统计结果
        TopGroups<BytesRef> groupsResult = c2.getTopGroups(docOffset);
        totalHitCount = groupsResult.totalHitCount;
        totalGroupedHitCount = groupsResult.totalGroupedHitCount;
        System.out.println("groupsResult.totalHitCount: " + totalHitCount);
        System.out.println("groupsResult.totalGroupedHitCount: " + totalGroupedHitCount);
        System.out.println("-----------------------");

        int groupIdx = 0;
        for (GroupDocs<BytesRef> groupDocs : groupsResult.groups) {
            groupIdx++;
            String groupValue = groupDocs.groupValue == null ? "分组域的域值为空" : groupDocs.groupValue.utf8ToString();
            System.out.println("group[" + groupIdx + "].groupFieldValue: " + groupValue);
            System.out.println("group[" + groupIdx + "].totalHits: " + groupDocs.totalHits);
            int docIdx = 0;
            for (ScoreDoc scoreDoc : groupDocs.scoreDocs) {
                docIdx++;
                System.out.println("group[" + groupIdx + "][" + docIdx + "]{docId:score}: " + scoreDoc.doc + "/" + scoreDoc.score);
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println("group[" + groupIdx + "][" + docIdx + "]{docId:author}: " + doc.get("id") + ":" + doc.get("content"));
                System.out.println("**********************");
            }
        }
    }

    private static void createIndex() throws IOException {
        directory = FSDirectory.open(Paths.get("D:/lucene/index"));
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, config);
        addDocuments(groupField, writer);
    }

    private static void addDocuments(String groupField, IndexWriter writer) throws IOException {
        // 0
        Document doc = new Document();
        addGroupField(doc, groupField, "author1");
        doc.add(new TextField("content", "random text", Field.Store.YES));
        doc.add(new StringField("id", "1", Field.Store.YES));
        writer.addDocument(doc);
        // 1
        doc = new Document();
        addGroupField(doc, groupField, "author1");
        doc.add(new TextField("content", "some more random text", Field.Store.YES));
        doc.add(new StringField("id", "2", Field.Store.YES));
        writer.addDocument(doc);
        // 2
        doc = new Document();
        addGroupField(doc, groupField, "author1");
        doc.add(new TextField("content", "some more random textual data", Field.Store.YES));
        doc.add(new StringField("id", "3", Field.Store.YES));
        writer.addDocument(doc);
        // 3
        doc = new Document();
        addGroupField(doc, groupField, "author2");
        doc.add(new TextField("content", "some random text", Field.Store.YES));
        doc.add(new StringField("id", "4", Field.Store.YES));
        writer.addDocument(doc);
        // 4
        doc = new Document();
        addGroupField(doc, groupField, "author3");
        doc.add(new TextField("content", "some more random text", Field.Store.YES));
        doc.add(new StringField("id", "5", Field.Store.YES));
        writer.addDocument(doc);
        // 5
        doc = new Document();
        addGroupField(doc, groupField, "author3");
        doc.add(new TextField("content", "random", Field.Store.YES));
        doc.add(new StringField("id", "6", Field.Store.YES));
        writer.addDocument(doc);
        // 6
        doc = new Document();
        doc.add(new TextField("content", "random word stuck in alot of other text", Field.Store.YES));
        doc.add(new StringField("id", "7", Field.Store.YES));
        writer.addDocument(doc);

        writer.close();
    }

    private static void addGroupField(Document doc, String groupField, String value) {
        doc.add(new StringField(groupField, value, Field.Store.YES));
        doc.add(new SortedDocValuesField(groupField, new BytesRef(value)));
    }
}
