package com.kevin.chap8.ext;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 类名: BooksMoreLikeThis<br/>
 * 包名：com.kevin.chap8.ext<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/28 9:54<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class BooksMoreLikeThis {

    public static void main(String[] args) throws IOException {
        String indexDir = "D:/lucene/index";
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        int numDocs = reader.maxDoc();

        MoreLikeThis mlt = new MoreLikeThis(reader);
        mlt.setFieldNames(new String[]{"subject"});
        mlt.setMinTermFreq(1);
        mlt.setMinDocFreq(1);

        for (int docId = 0; docId < numDocs; docId++) {
            System.out.println();
            Document doc = reader.document(docId);
            System.out.println(doc.get("title"));

            Query query = mlt.like(docId);
            System.out.println("query=" + query);

            TopDocs similarDocs = searcher.search(query, 10);
            if (similarDocs.totalHits == 1) {   // 忽略重复文档
                System.out.println("None like this");
            }
            for (int i = 0; i < similarDocs.scoreDocs.length; i++) {
                if (similarDocs.scoreDocs[i].doc != docId) {    // 忽略重复文档
                    doc = reader.document(similarDocs.scoreDocs[i].doc);
                    System.out.println("->" + doc.getField("title").stringValue());
                }
            }
        }

        reader.close();
        dir.close();
    }
}
