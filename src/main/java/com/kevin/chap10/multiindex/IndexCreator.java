package com.kevin.chap10.multiindex;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CountDownLatch;

/**
 * 类名: IndexCreator<br/>
 * 包名：com.kevin.chap10<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/27 9:38<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class IndexCreator implements Runnable {

    /** 需要读取的文件的存放目录 */
    private String docPath;
    /** 索引文件存放目录 */
    private String indexDir;
    private CountDownLatch latch1;
    private CountDownLatch latch2;

    public IndexCreator(String docPath, String indexDir, CountDownLatch latch1,
                        CountDownLatch latch2) {
        this.docPath = docPath;
        this.indexDir = indexDir;
        this.latch1 = latch1;
        this.latch2 = latch2;
    }

    @Override
    public void run() {
        IndexWriter writer = null;
        try {
            latch1.await();
            Analyzer analyzer = new IKAnalyzer();
            Directory directory = FSDirectory.open(Paths.get(indexDir));
            IndexWriterConfig config = new IndexWriterConfig(analyzer)
                    .setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(directory, config);
            indexDocs(writer, Paths.get(docPath));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            latch2.countDown();
        }
    }

    private void indexDocs(IndexWriter writer, Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
                        throws IOException {
                    System.out.println(path.getFileName());
                    indexDoc(writer, path, attrs.lastModifiedTime().toMillis());
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            indexDoc(writer, path, Files.getLastModifiedTime(path,
                    LinkOption.NOFOLLOW_LINKS).toMillis());
        }
    }

    private void indexDoc(IndexWriter writer, Path path, long lastModifiedTime)
            throws IOException {
        Document doc = new Document();
        doc.add(new StringField("path", path.toString(), Field.Store.YES));
        doc.add(new LongPoint("modified", lastModifiedTime));
        doc.add(new StoredField("modified", lastModifiedTime));
        doc.add(new TextField("contents", readFile(path), Field.Store.YES));
        if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
            System.out.println("adding " + path);
            writer.addDocument(doc);
        } else {
            System.out.println("updating " + path);
            writer.updateDocument(new Term("path", path.toString()), doc);
        }
        writer.commit();
    }

    private String readFile(Path path) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader br = Files.newBufferedReader(path);
        String line;
        while ((line = br.readLine()) != null) {
            builder.append(line).append("\n");
        }
        br.close();
        return builder.toString();
    }
}
