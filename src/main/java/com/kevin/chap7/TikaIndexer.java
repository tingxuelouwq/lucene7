package com.kevin.chap7;

import com.kevin.chap1.IndexerForLuceneAction;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @类名: TikaIndexer
 * @包名：com.kevin.chap7
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/26 9:37
 * @版本：1.0
 * @描述：
 */
public class TikaIndexer extends IndexerForLuceneAction {

    public static void main(String[] args) throws Exception {
//        printParser();
        String dataDir = "D:\\Workspace\\Idea\\lucene7\\src\\main\\java\\com\\kevin\\chap7\\data";
        String indexDir = "D:\\Lucene\\index";
        long start = System.currentTimeMillis();
        TikaIndexer indexer = new TikaIndexer(indexDir);
        int numIndexed;
        try {
            numIndexed = indexer.index(dataDir);
        } finally {
            indexer.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("Indexing " + numIndexed + " files took "
                + (end - start) + " milliseconds");
    }

    public TikaIndexer(String indexDir) throws IOException {
        super(indexDir);
    }

    public static void printParser() {
        TikaConfig config = TikaConfig.getDefaultConfig();
        Map<MediaType, Parser> parserMap = ((CompositeParser) config.getParser()).getParsers();
        for (Map.Entry<MediaType, Parser> entry : parserMap.entrySet()) {
            MediaType mediaType = entry.getKey();
            Parser parser = entry.getValue();
            System.out.println(mediaType + ":" + parser);
        }
        System.out.println();
    }

    @Override
    protected Document getDocument(String rootDir, File file) throws Exception{
        Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
        InputStream is = new FileInputStream(file);
        AutoDetectParser parser = new AutoDetectParser();
        ContentHandler handler = new BodyContentHandler();
        ParseContext context = new ParseContext();
        context.set(Parser.class, parser);

        try {
            parser.parse(is, handler, metadata, context);
        } finally {
            is.close();
        }

        Document doc = new Document();
        doc.add(new TextField("contents", handler.toString(), Field.Store.NO));
        doc.add(new TextField("filename", file.getCanonicalPath(), Field.Store.YES));

        System.out.println("all text:\n" + handler.toString());
        for (String name : metadata.names()) {
            String value = metadata.get(name);
            System.out.println(name + ":" + value);
        }
        System.out.println("-----------");

        return doc;
    }
}
