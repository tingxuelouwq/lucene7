package com.kevin.chap7.sax;

import com.kevin.chap7.digester.DocumentHandlerException;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

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
    private Map<String, Map<String, String>> attrMap = new HashMap<>();
    private Document doc;

    public static void main(String[] args) throws Exception {
        SAXXMLDocument handler = new SAXXMLDocument();
        Document doc = handler.getDocument(SAXXMLDocument.class.getClassLoader()
                .getResourceAsStream("data/addressbook.xml"));

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
        int numAttrs = attrs.getLength();
        if (numAttrs > 0) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < numAttrs; i++) {
                map.put(attrs.getQName(i), attrs.getValue(i));
            }
            attrMap.put(qName, map);
        }
    }

    public void characters(char[] text, int start, int length) {
        elementBuffer.append(text, start, length);
    }

    public void endElement(String uri, String localName, String qName) {
        Map<String, String> map = attrMap.get(qName);
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> attr : map.entrySet()) {
                String attrName = attr.getKey();
                String attrValue = attr.getValue();
                doc.add(new StringField(attrName, attrValue, Field.Store.YES));
            }
        }
        map = null;

        String body = elementBuffer.toString().trim();
        if (!StringUtils.isEmpty(body)) {
            doc.add(new StringField(qName, body, Field.Store.YES));
        }
        elementBuffer.setLength(0);
    }
}
