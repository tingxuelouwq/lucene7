package com.kevin.chap8.facet;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.*;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名: SimpleFacetsExample<br/>
 * 包名：com.kevin.facet<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/19 10:10<br/>
 * 版本：1.0<br/>
 * 描述：Shows simple usage of faceted indexing and search.<br/>
 */
public class SimpleFacetsExample {

    private final Directory indexDir = FSDirectory.open(Paths.get("D:/lucene/index"));
    private final Directory taxoDir = FSDirectory.open(Paths.get("D:/lucene/facet"));
    private final FacetsConfig config = new FacetsConfig();

    /** Empty constructor */
    public SimpleFacetsExample() throws IOException {
        config.setHierarchical("Publish Date", true);
    }

    /** Build the example index. */
    private void index() throws IOException {
        IndexWriter indexWriter = new IndexWriter(indexDir, new IndexWriterConfig(
                new WhitespaceAnalyzer()).setOpenMode(IndexWriterConfig.OpenMode.CREATE));

        // Writes facet ords to a separate directory from the main index
        DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir,
                IndexWriterConfig.OpenMode.CREATE);

        Document doc = new Document();
        doc.add(new FacetField("Author", "Bob"));
        doc.add(new FacetField("Publish Date", "2010", "10", "15"));
        indexWriter.addDocument(config.build(taxoWriter, doc));

        doc = new Document();
        doc.add(new FacetField("Author", "Lisa"));
        doc.add(new FacetField("Publish Date", "2010", "10", "20"));
        indexWriter.addDocument(config.build(taxoWriter, doc));

        doc = new Document();
        doc.add(new FacetField("Author", "Lisa"));
        doc.add(new FacetField("Publish Date", "2012", "1", "1"));
        indexWriter.addDocument(config.build(taxoWriter, doc));

        doc = new Document();
        doc.add(new FacetField("Author", "Susan"));
        doc.add(new FacetField("Publish Date", "2012", "1", "7"));
        indexWriter.addDocument(config.build(taxoWriter, doc));

        doc = new Document();
        doc.add(new FacetField("Author", "Frank"));
        doc.add(new FacetField("Publish Date", "1999", "5", "5"));
        indexWriter.addDocument(config.build(taxoWriter, doc));

        indexWriter.close();
        taxoWriter.close();
    }

    /** User runs a query and counts facets. */
    private List<FacetResult> facetsWithSearch() throws IOException {
        DirectoryReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

        FacetsCollector fc = new FacetsCollector();

        // MatchAllDocsQuery is for "browsing" (counts facets
        // for all non-deleted docs in the index); normally
        // you'd use a "normal" query:
        FacetsCollector.search(searcher, new MatchAllDocsQuery(), 10, fc);

        // Retrieve results
        List<FacetResult> results = new ArrayList<>();

        // Count both "Publish Date" and "Author" dimensions
        Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);
        results.add(facets.getTopChildren(10, "Author"));
        results.add(facets.getTopChildren(10, "Publish Date"));

        indexReader.close();
        taxoReader.close();

        return results;
    }

    /** User runs a query and counts facets only without collecting the matching document. */
    private List<FacetResult> facetsOnly() throws IOException {
        DirectoryReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

        FacetsCollector fc = new FacetsCollector();

        // MatchAllDocsQuery is for "browsing" (counts facets
        // for all non-deleted docs in the index); normally
        // you'd use a "normal" query:
        searcher.search(new MatchAllDocsQuery(), fc);

        // Retrieve results
        List<FacetResult> results = new ArrayList<>();

        // Count both "Publish Date" and "Author" dimensions
        Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);

        results.add(facets.getTopChildren(10, "Author"));
        results.add(facets.getTopChildren(10, "Publish Date"));

        indexReader.close();
        taxoReader.close();

        return results;
    }

    /** User drills down on 'Publish Date/2010', and we
     * return facets for 'Author'
     */
    private FacetResult drillDown() throws IOException {
        DirectoryReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

        // Passing no baseQuery means we drill down on all
        // documents ("browse only"):
        DrillDownQuery q = new DrillDownQuery(config);

        // Now user drills down on Publish Date/2010.
        q.add("Publish Date", "2010");
        FacetsCollector fc = new FacetsCollector();
        FacetsCollector.search(searcher, q, 10, fc);

        // Retrieve results
        Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);
        FacetResult result = facets.getTopChildren(10, "Author");

        indexReader.close();
        taxoReader.close();

        return result;
    }

    /** User drills down on 'Publish Date/2010', and we
     * return facets for both 'Publish Date' and 'Author',
     * using DrillSideways.
     *
     * DrillSidewasy: Computes drill down and sideways counts for the provided DrillDownQuery.
     * Drill sideways counts include alternative values/aggregates for the drill-down dimensions
     * so that a dimension does not disappear after the user drills down into it.
     * 也就是说，假如DrillDownQuery为("Publish Date", "2010")，表示用户会深入到Publish Date/2010中，但
     * 如果使用DrillSideways，则除了计数Publish Date/2010外，还会计数Publish Date
     */
    private List<FacetResult> drillSideways() throws IOException {
        DirectoryReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

        // Passing no baseQuery means we drill down on all
        // documents ("browse only"):
        DrillDownQuery q = new DrillDownQuery(config);

        // Now user drills down on Publish Date/2010
        q.add("Publish Date", "2010");

        DrillSideways ds = new DrillSideways(searcher, config, taxoReader);
        DrillSideways.DrillSidewaysResult result = ds.search(q, 10);

        // Retrieve results
        List<FacetResult> facets = result.facets.getAllDims(10);

        indexReader.close();
        taxoReader.close();

        return facets;
    }

    /** Runs the search example. */
    public List<FacetResult> runFacetOnly() throws IOException {
        index();
        return facetsOnly();
    }

    /** Runs the search example */
    public List<FacetResult> runSearch() throws IOException {
        index();
        return facetsWithSearch();
    }

    /** Runs the drill-down example. */
    public FacetResult runDrillDown() throws IOException {
        index();
        return drillDown();
    }

    /** Runs the drill-sideways example. */
    public List<FacetResult> runDrillSideways() throws IOException {
        index();
        return drillSideways();
    }

    public static void main(String[] args) throws IOException {
        SimpleFacetsExample example = new SimpleFacetsExample();

        System.out.println("Facet counting example:");
        System.out.println("-----------------------");
        List<FacetResult> results1 = example.runFacetOnly();
        System.out.println("Author: " + results1.get(0));
        System.out.println("Publish Date: " + results1.get(1));

        System.out.println("Facet counting example (combined facets and search):");
        System.out.println("-----------------------");
        List<FacetResult> results = example.runSearch();
        System.out.println("Author: " + results.get(0));
        System.out.println("Publish Date: " + results.get(1));

        System.out.println("Facet drill-down example (Publish Date/2010):");
        System.out.println("---------------------------------------------");
        System.out.println("Author: " + example.runDrillDown());

        System.out.println("Facet drill-sideways example (Publish Date/2010):");
        System.out.println("---------------------------------------------");
        for(FacetResult result : example.runDrillSideways()) {
            System.out.println(result);
        }
    }
}
