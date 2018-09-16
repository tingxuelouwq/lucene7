package com.kevin.chap8.facet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.*;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名: SimpleFacetsTest<br/>
 * 包名：com.kevin.chap8.facet<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/16 10:56<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class SimpleFacetsTest {

    private Directory indexDir = new RAMDirectory();
    private Directory taxoDir = new RAMDirectory();
    private FacetsConfig facetsConfig = new FacetsConfig();

    public SimpleFacetsTest() {
        facetsConfig.setHierarchical("Author", true);
        facetsConfig.setHierarchical("Publish Date", true);
    }

    private void index() throws IOException {
        Analyzer analyzer = new WhitespaceAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(indexDir, indexWriterConfig);
        DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);

        Document doc = new Document();
        doc.add(new FacetField("Author", new String[]{"Bob"}));
        doc.add(new FacetField("Publish Date", new String[]{"2010", "10", "15"}));
        indexWriter.addDocument(facetsConfig.build(taxoWriter, doc));

        doc = new Document();
        doc.add(new FacetField("Author", new String[] { "Lisa" }));
        doc.add(new FacetField("Publish Date", new String[]{"2010", "10", "20"}));
        indexWriter.addDocument(facetsConfig.build(taxoWriter, doc));

        doc = new Document();
        doc.add(new FacetField("Author", new String[] { "Lisa" }));
        doc.add(new FacetField("Publish Date", new String[] { "2012", "1", "1" }));
        indexWriter.addDocument(facetsConfig.build(taxoWriter, doc));

        doc = new Document();
        doc.add(new FacetField("Author", new String[] { "Susan" }));
        doc.add(new FacetField("Publish Date", new String[] { "2012", "1", "7" }));
        indexWriter.addDocument(facetsConfig.build(taxoWriter, doc));

        doc = new Document();
        doc.add(new FacetField("Author", new String[] { "Frank" }));
        doc.add(new FacetField("Publish Date", new String[] { "1999", "5", "5" }));
        indexWriter.addDocument(facetsConfig.build(taxoWriter, doc));

        indexWriter.close();
        taxoWriter.close();
    }

    private List<FacetResult> facetsWithSearch() throws IOException {
        IndexReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);
        FacetsCollector fc = new FacetsCollector();
        FacetsCollector.search(searcher, new MatchAllDocsQuery(), 10, fc);
        List<FacetResult> results = new ArrayList<>();
        Facets facets = new FastTaxonomyFacetCounts(taxoReader, facetsConfig, fc);
        results.add(facets.getTopChildren(10, "Author", new String[0]));
        results.add(facets.getTopChildren(10, "Publish Date", new String[0]));
        indexReader.close();
        taxoReader.close();
        return results;
    }

    private List<FacetResult> facetsOnly() throws IOException {
        IndexReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);
        FacetsCollector fc = new FacetsCollector();
        searcher.search(new MatchAllDocsQuery(), fc);
        List<FacetResult> results = new ArrayList<FacetResult>();
        Facets facets = new FastTaxonomyFacetCounts(taxoReader, facetsConfig, fc);
        results.add(facets.getTopChildren(10, "Author"));
        results.add(facets.getTopChildren(10, "Publish Date"));
        indexReader.close();
        taxoReader.close();
        return results;
    }

    private FacetResult drillDown() throws IOException {
        IndexReader indexReader = DirectoryReader.open(this.indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(this.taxoDir);
        DrillDownQuery q = new DrillDownQuery(facetsConfig);
        q.add("Publish Date", new String[] { "2010" });
        FacetsCollector fc = new FacetsCollector();
        FacetsCollector.search(searcher, q, 10, fc);
        Facets facets = new FastTaxonomyFacetCounts(taxoReader, facetsConfig, fc);
        FacetResult result = facets.getTopChildren(10, "Author", new String[0]);
        indexReader.close();
        taxoReader.close();
        return result;
    }

    private List<FacetResult> drillSideways() throws IOException {
        IndexReader indexReader = DirectoryReader.open(this.indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(this.taxoDir);
        DrillDownQuery q = new DrillDownQuery(facetsConfig);
        q.add("Publish Date", new String[] { "2010" });
        DrillSideways ds = new DrillSideways(searcher, facetsConfig, taxoReader);
        DrillSideways.DrillSidewaysResult result = ds.search(q, 10);
        List<FacetResult> facets = result.facets.getAllDims(10);
        indexReader.close();
        taxoReader.close();
        return facets;
    }

    public List<FacetResult> runFacetOnly() throws IOException {
        index();
        return facetsOnly();
    }

    public List<FacetResult> runSearch() throws IOException {
        index();
        return facetsWithSearch();
    }

    public FacetResult runDrillDown() throws IOException {
        index();
        return drillDown();
    }

    public List<FacetResult> runDrillSideways() throws IOException {
        index();
        return drillSideways();
    }

    public static void main(String[] args) throws Exception {
        // one
        System.out.println("Facet counting test:");
        System.out.println("-----------------------");
        SimpleFacetsTest test = new SimpleFacetsTest();
        List<FacetResult> results1 = test.runFacetOnly();
        System.out.println("Author: " + results1.get(0));
        System.out.println("Publish Date: " + results1.get(1));
        // two
        System.out.println("Facet counting test (combined facets and search):");
        System.out.println("-----------------------");
        List<FacetResult> results = test.runSearch();
        System.out.println("Author: " + results.get(0));
        System.out.println("Publish Date: " + results.get(1));
        // three
        System.out.println("Facet drill-down test (Publish Date/2010):");
        System.out.println("---------------------------------------------");
        System.out.println("Author: " + test.runDrillDown());
        // four
        System.out.println("Facet drill-sideways test (Publish Date/2010):");
        System.out.println("---------------------------------------------");
        for (FacetResult result : test.runDrillSideways()) {
            System.out.println(result);
        }
    }
}
