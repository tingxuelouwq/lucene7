package com.kevin.chap7.digester;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.annotations.FromAnnotationsRuleModule;
import org.apache.commons.digester3.binder.DigesterLoader;
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
import java.util.List;

/**
 * @类名: DigesterXMLDocument
 * @包名：com.kevin.chap7
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/26 15:41
 * @版本：1.0
 * @描述：
 */
public class DigesterXMLDocument {

    private static final DigesterLoader LOADER = DigesterLoader.newLoader(new FromAnnotationsRuleModule() {
        @Override
        protected void configureRules() {
            bindRulesFrom(AddressBook.class);
        }
    });

    private Directory dir;
    private IndexWriter writer;

    public DigesterXMLDocument(String indexDir) throws IOException {
        dir = FSDirectory.open(Paths.get(indexDir));
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        writer = new IndexWriter(dir, config);
    }

    private void close() throws IOException {
        writer.close();
        dir.close();
    }

    public void addDocument(AddressBook addressBook) throws IOException {
        List<Contact> contacts = addressBook.getContacts();
        for (Contact contact : contacts) {
            Document doc = new Document();
            doc.add(new StringField("type", contact.getType(), Field.Store.YES));
            doc.add(new StringField("name", contact.getName(), Field.Store.YES));
            doc.add(new StringField("address", contact.getAddress(), Field.Store.YES));
            doc.add(new StringField("city", contact.getCity(), Field.Store.YES));
            doc.add(new StringField("province", contact.getProvince(), Field.Store.YES));
            doc.add(new StringField("postalcode", contact.getPostalcode(), Field.Store.YES));
            doc.add(new StringField("country", contact.getCountry(), Field.Store.YES));
            doc.add(new StringField("telephone", contact.getTelephone(), Field.Store.YES));
            writer.addDocument(doc);
        }
        close();
    }

    public static void main(String[] args) throws IOException, SAXException {
        Digester digester = LOADER.newDigester();
        AddressBook addressBook = digester.parse(DigesterXMLDocument.class.getClassLoader()
                .getResourceAsStream("data/addressbook.xml"));

        String indexDir = "D:\\lucene\\index";
        DigesterXMLDocument digesterXMLDocument = new DigesterXMLDocument(indexDir);
        digesterXMLDocument.addDocument(addressBook);
    }
}
