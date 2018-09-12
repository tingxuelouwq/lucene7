package com.kevin.chap8.grouping;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.BytesRefFieldSource;
import org.apache.lucene.search.*;
import org.apache.lucene.search.grouping.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.mutable.MutableValueStr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @类名: TwoPhaseGroupingTest
 * @包名：com.kevin.chap8.grouping
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/8/6 17:09
 * @版本：1.0
 * @描述：
 */
public class TwoPhaseGroupingTest {

    private static Directory directory;
    private static String indexDir = "D:/lucene/index";
    private static final String groupField = "author";

    public static void main(String[] args) throws IOException {
        directory = GroupingUtil.createIndex(indexDir, groupField);
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = new TermQuery(new Term("content", "random"));
        Sort groupSort = Sort.RELEVANCE;
        groupBy(searcher, query, groupSort);
//        groupSearch(searcher, query, groupSort);
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
        FirstPassGroupingCollector<BytesRef> c1 = new FirstPassGroupingCollector<>(
                new TermGroupSelector(groupField), groupSort, groupOffset + topNGroups);
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
                System.out.println("SearchGroup(groupValue=" + (groupValue == null ? null :
                        groupValue.utf8ToString())+ " sortValues=" + Arrays.toString(sortValues) + ")");
            }
            System.out.println("-----------------------");
        }

        Collector secondPassGroupingCollector = null;
        TopGroupsCollector<BytesRef> c2 = new TopGroupsCollector<>(new TermGroupSelector(groupField),
                topGroups, groupSort, docSort, docsPerGroup, getScores, getMaxScores, fillFields);
        /**
         * 如果计算总的分组数量，则需要把SecondPassGroupingCollector包装AllGroupsCollector
         */
        AllGroupsCollector c3 = null;
        if (requiredTotalGroupCount) {
            c3 = new AllGroupsCollector(new TermGroupSelector(groupField));
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
            String groupValue = groupDocs.groupValue == null ? "分组域的域值为空" :
                    groupDocs.groupValue.utf8ToString();
            System.out.println("group[" + groupIdx + "].groupFieldValue: " + groupValue);
            System.out.println("group[" + groupIdx + "].totalHits: " + groupDocs.totalHits);
            int docIdx = 0;
            for (ScoreDoc scoreDoc : groupDocs.scoreDocs) {
                docIdx++;
                System.out.println("group[" + groupIdx + "][" + docIdx + "]{docId:score}: "
                        + scoreDoc.doc + "/" + scoreDoc.score);
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println("group[" + groupIdx + "][" + docIdx + "]{docId:content}: "
                        + doc.get("id") + ":" + doc.get("content"));
                System.out.println("**********************");
            }
        }
    }

    private static void groupSearch(IndexSearcher searcher, Query query, Sort groupSort) throws IOException {
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

        ValueSource valueSource = new BytesRefFieldSource(groupField);
        Map<?, ?> context = new HashMap<>();
        FirstPassGroupingCollector c1 = new FirstPassGroupingCollector(
                new ValueSourceGroupSelector(valueSource, context), groupSort, topNGroups);
        CachingCollector cachingCollector = CachingCollector.create(c1, cacheScores, maxCacheRAMMB);
        // 第一次分组统计
        searcher.search(query, cachingCollector);

        // 打印第一次分组统计的结果
        Collection<SearchGroup<MutableValueStr>> topGroups = c1.getTopGroups(groupOffset, fillFields);
        if (topGroups == null) {
            System.out.println("No groups matched");
            return;
        } else {
            for (SearchGroup<MutableValueStr> searchGroup : topGroups) {
                if (searchGroup.groupValue.exists) {
                    String groupValue = searchGroup.groupValue.toString();
                    System.out.println("groupValue:" + groupValue);
                } else {
                    System.out.println("groupValue:null");
                }
                for (int i = 0; i < searchGroup.sortValues.length; i++) {
                    System.out.println("sortValues:"
                            + searchGroup.sortValues[i]);
                }
            }
            System.out.println("-----------------------");
        }

        Collector secondPassGroupingCollector = null;
        TopGroupsCollector c2 = new TopGroupsCollector(new ValueSourceGroupSelector(valueSource, context),
                topGroups, groupSort, docSort, docsPerGroup, getScores, getMaxScores, fillFields);
        /**
         * 如果计算总的分组数量，则需要把SecondPassGroupingCollector包装AllGroupsCollector
         */
        AllGroupsCollector c3 = null;
        if (requiredTotalGroupCount) {
            c3 = new AllGroupsCollector(new ValueSourceGroupSelector(valueSource, context));
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
        TopGroups<MutableValueStr> groupsResult = c2.getTopGroups(docOffset);
        totalHitCount = groupsResult.totalHitCount;
        totalGroupedHitCount = groupsResult.totalGroupedHitCount;
        System.out.println("groupsResult.totalHitCount: " + totalHitCount);
        System.out.println("groupsResult.totalGroupedHitCount: " + totalGroupedHitCount);
        System.out.println("-----------------------");

        int groupIdx = 0;
        for (GroupDocs<MutableValueStr> groupDocs : groupsResult.groups) {
            groupIdx++;
            String groupValue;
            if (groupDocs.groupValue.exists()) {
                groupValue = groupDocs.groupValue.toString();
            } else {
                groupValue = "分组域的域值为空";
            }
            System.out.println("group[" + groupIdx + "].groupFieldValue: " + groupValue);
            System.out.println("group[" + groupIdx + "].totalHits: " + groupDocs.totalHits);
            int docIdx = 0;
            for (ScoreDoc scoreDoc : groupDocs.scoreDocs) {
                docIdx++;
                System.out.println("group[" + groupIdx + "][" + docIdx + "]{docId:score}: "
                        + scoreDoc.doc + "/" + scoreDoc.score);
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println("group[" + groupIdx + "][" + docIdx + "]{docId:content}: "
                        + doc.get("id") + ":" + doc.get("content"));
                System.out.println("**********************");
            }
        }
    }
}
