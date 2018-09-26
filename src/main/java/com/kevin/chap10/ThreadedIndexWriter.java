package com.kevin.chap10;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * 类名: ThreadedIndexWriter<br/>
 * 包名：com.kevin.chap10<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/26 18:56<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class ThreadedIndexWriter extends IndexWriter {

    private ExecutorService threadPool;
    private Analyzer analyzer;

    private class Job implements Runnable {

        Document doc;
        Term delTerm;

        Job(Document doc, Term delTerm) {
            this.doc = doc;
            this.delTerm = delTerm;
        }

        @Override
        public void run() {
            try {
                if (delTerm != null) {
                    ThreadedIndexWriter.super.updateDocument(delTerm, doc);
                } else {
                    ThreadedIndexWriter.super.addDocument(doc);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ThreadedIndexWriter(Directory d, IndexWriterConfig conf,
                               Analyzer analyzer, int numThreads) throws IOException {
        super(d, conf);
        this.analyzer = analyzer;
        threadPool = Executors.newFixedThreadPool(numThreads);
    }

    public void addDocument(Document doc) {
        threadPool.submit(new Job(doc, null));
    }

    public void updateDocument(Term term, Document doc) {
        threadPool.submit(new Job(doc, term));
    }
}
