package com.kevin.chap7;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @类名: SAXXMLDocument
 * @包名：com.kevin.chap7
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/26 14:11
 * @版本：1.0
 * @描述：
 */
public class SAXXMLDocument extends DefaultHandler {

    private StringBuilder elementBuffer = new StringBuilder();
    private Map<String, String> attrMap = new HashMap<>();
    private Document doc;

    public static void main(String[] args) throws Exception {
        String filePath = "D:\\Workspace\\Idea\\lucene7\\src\\main\\java\\com\\kevin\\chap7\\data\\addressbook.xml";
        SAXXMLDocument handler = new SAXXMLDocument();
        Document doc = handler.getDocument(new FileInputStream(new File(filePath)));

        String indexDir = "D:\\Lucene\\index";
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, config);
        writer.addDocument(doc);
        writer.close();
        dir.close();
    }

    private Document getDocument(InputStream is) throws DocumentHandlerException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser parser = spf.newSAXParser();
            parser.parse(is, this);
        } catch (Exception e) {
            throw new DocumentHandlerException("Cannot parse XML document", e);
        }
        return doc;
    }

    public void startDocument() {
        doc = new Document();
    }

    public void startElement(String uri, String localName, String qName, Attributes attrs) {
        elementBuffer.setLength(0);
        attrMap.clear();
        int numAttrs = attrs.getLength();
        if (numAttrs > 0) {
            for (int i = 0; i < numAttrs; i++) {
                attrMap.put(attrs.getQName(i), attrs.getValue(i));
            }
        }
    }

    public void characters(char[] text, int start, int length) {
        elementBuffer.append(text, start, length);
    }

    public void endElement(String uri, String localName, String qName) {
        if (qName.endsWith("address-book")) {
            return;
        } else if (qName.endsWith("contact")) {
            for (Map.Entry<String, String> attr : attrMap.entrySet()) {
                String attrName = attr.getKey();
                String attrValue = attr.getValue();
                doc.add(new StringField(attrName, attrValue, Field.Store.YES));
            }
        } else {
            doc.add(new StringField(qName, elementBuffer.toString(), Field.Store.YES));
        }
    }
}
