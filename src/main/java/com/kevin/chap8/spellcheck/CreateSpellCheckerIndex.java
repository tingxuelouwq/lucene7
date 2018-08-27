package com.kevin.chap8.spellcheck;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 类名: CreateSpellCheckerIndex<br/>
 * 包名：com.kevin.chap8.spellcheck<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/27 15:00<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class CreateSpellCheckerIndex {

    public static void main(String[] args) throws IOException {
        String spellCheckDir = "D:\\lucene\\spellcheck";
        String indexField = "title";

        System.out.println("Now build SpellChecker index...");
        Directory dir = FSDirectory.open(Paths.get(spellCheckDir));
        SpellChecker spell = new SpellChecker(dir);
        long startTime = System.currentTimeMillis();

        Directory dir2 = createIndex(indexField);
        IndexReader reader = DirectoryReader.open(dir2);
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        spell.indexDictionary(new LuceneDictionary(reader, indexField), config, true);
        dir.close();
        dir2.close();
        long endTime = System.currentTimeMillis();
        System.out.println(spell.exist("lettuce"));
        System.out.println("Took " + (endTime - startTime) + " milliseconds");
    }

    private static Directory createIndex(String indexField) throws IOException {
        Directory dir = new RAMDirectory();
        IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig());
        Document doc = new Document();
        doc.add(new TextField(indexField, "lettuce", Field.Store.YES));
        doc.add(new TextField(indexField, "letch", Field.Store.YES));
        doc.add(new TextField(indexField, "deduce", Field.Store.YES));
        doc.add(new TextField(indexField, "letup", Field.Store.YES));
        doc.add(new TextField(indexField, "seduce", Field.Store.YES));
        doc.add(new TextField(indexField, "find", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();
        return dir;
    }
}
