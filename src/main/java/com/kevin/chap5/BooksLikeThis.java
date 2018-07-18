package com.kevin.chap5;

import com.kevin.util.TestUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

/**
 * @类名: BooksLikeThis
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/16 14:36
 * @版本：1.0
 * @描述：
 */
public class BooksLikeThis {

    private IndexReader reader;
    private IndexSearcher searcher;

    public BooksLikeThis(IndexReader reader) {
        this.reader = reader;
        this.searcher = new IndexSearcher(reader);
    }

    public static void main(String[] args) throws IOException {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexReader reader = DirectoryReader.open(dir);
        int numDocs = reader.maxDoc();
        BooksLikeThis blt = new BooksLikeThis(reader);
        for (int i = 0; i < numDocs; i++) { // 遍历每本书
            Document doc = reader.document(i);
            System.out.println(doc.get("title"));
            Document[] docs = blt.docsLike(i);  // 查找与这本书相似的书
            if (docs.length == 0) {
                System.out.println("  None like this");
            }
            for (Document likeThisDoc : docs) {
                System.out.println("  -> " + likeThisDoc.get("title"));
            }
        }
        reader.close();
        dir.close();
    }

    public Document[] docsLike(int id) throws IOException {
        Document doc = reader.document(id);
        String[] authors = doc.getValues("author");
        BooleanQuery.Builder authorQueryBuilder = new BooleanQuery.Builder();
        for (String author : authors) {
            authorQueryBuilder.add(new TermQuery(new Term("author", author)),
                    BooleanClause.Occur.SHOULD);
        }
        FunctionScoreQuery authorQuery = new FunctionScoreQuery(authorQueryBuilder.build(),
                DoubleValuesSource.constant(2.0));  // 对作者相同的书进行加权

        BooleanQuery.Builder subjectQueryBuilder = new BooleanQuery.Builder();
        Terms terms = reader.getTermVector(id, "subject");
        TermsEnum termsEnum = terms.iterator();
        BytesRef thisTerm;
        while ((thisTerm = termsEnum.next()) != null) { // 使用subject词条向量
            String termText = thisTerm.utf8ToString();
            TermQuery termQuery = new TermQuery(new Term("subject", termText));
            subjectQueryBuilder.add(termQuery, BooleanClause.Occur.SHOULD);
        }
        BooleanQuery subjectQuery = subjectQueryBuilder.build();

        BooleanQuery.Builder likeThisQuery = new BooleanQuery.Builder();
        likeThisQuery.add(authorQuery, BooleanClause.Occur.SHOULD);
        likeThisQuery.add(subjectQuery, BooleanClause.Occur.SHOULD);
        likeThisQuery.add(new TermQuery(new Term("isbn", doc.get("isbn"))),
                BooleanClause.Occur.MUST_NOT);  // 排除当前这本书

        TopDocs hits = searcher.search(likeThisQuery.build(), 10);
        int size = hits.scoreDocs.length;
        Document[] docs = new Document[size];
        for (int i = 0; i < size; i++) {
            docs[i] = reader.document(hits.scoreDocs[i].doc);
        }
        return docs;
    }
}
