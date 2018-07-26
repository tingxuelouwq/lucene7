package com.kevin.chap7;

import org.apache.commons.digester3.Digester;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.file.Paths;

/**
 * @类名: DigesterXMLDocument
 * @包名：com.kevin.chap7
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/26 15:41
 * @版本：1.0
 * @描述：
 */
public class DigesterXMLDocument {

    private Digester digester;
    private static Document doc;

    public DigesterXMLDocument() {
        digester = new Digester();
        digester.setValidating(true);
        digester.addObjectCreate("address-book", DigesterXMLDocument.class);
        digester.addObjectCreate("address-book/contact", Contact.class);
        digester.addSetProperties("address-book/contact",
                "type", "type");
        digester.addCallMethod("address-book/contact/name",
                "setName", 0);
        digester.addCallMethod("address-book/contact/address",
                "setAddress", 0);
        digester.addCallMethod("address-book/contact/city",
                "setCity", 0);
        digester.addCallMethod("address-book/contact/province",
                "setProvince", 0);
        digester.addCallMethod("address-book/contact/postalcode",
                "setPostalcode", 0);
        digester.addCallMethod("address-book/contact/country",
                "setCountry", 0);
        digester.addCallMethod("address-book/contact/telephone",
                "setTelephone", 0);
        digester.addSetNext("address-book/contact", "populateDocument");
    }

    public void populateDocument(Contact contact) {
        doc = new Document();
        doc.add(new StringField("type", contact.getType(), Field.Store.YES));
        doc.add(new StringField("name", contact.getName(), Field.Store.YES));
        doc.add(new StringField("address", contact.getAddress(), Field.Store.YES));
        doc.add(new StringField("city", contact.getCity(), Field.Store.YES));
        doc.add(new StringField("province", contact.getProvince(), Field.Store.YES));
        doc.add(new StringField("postalcode", contact.getPostalcode(), Field.Store.YES));
        doc.add(new StringField("country", contact.getCountry(), Field.Store.YES));
        doc.add(new StringField("telephone", contact.getTelephone(), Field.Store.YES));
    }

    public synchronized Document getDocument(InputStream is) throws DocumentHandlerException {
        try {
            digester.parse(is);
        } catch (SAXException e) {
            throw new DocumentHandlerException("Cannot parse XML document", e);
        } catch (IOException e) {
            throw new DocumentHandlerException("Cannot parse XML document", e);
        }
        return doc;
    }

    public static void main(String[] args) throws Exception {
        String filePath = "D:\\Workspace\\Idea\\lucene7\\src\\main\\java\\com\\kevin\\chap7\\data\\addressbook.xml";
        DigesterXMLDocument handler = new DigesterXMLDocument();
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
}
