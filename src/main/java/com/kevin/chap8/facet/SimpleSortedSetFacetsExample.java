package com.kevin.chap8.facet;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.*;
import org.apache.lucene.facet.sortedset.DefaultSortedSetDocValuesReaderState;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetCounts;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesReaderState;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
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
 * 类名: SimpleSortedSetFacetsExample<br/>
 * 包名：com.kevin.chap8.facet<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/20 14:16<br/>
 * 版本：1.0<br/>
 * 描述：Shows simple usage of faceted indexing and search.<br/>
 */
public class SimpleSortedSetFacetsExample {

    private final Directory indexDir = FSDirectory.open(Paths.get("D:/lucene/index"));
    private final FacetsConfig config = new FacetsConfig();

    /** Empty constructor */
    public SimpleSortedSetFacetsExample() throws IOException {
    }

    /** Build the example index. */
    private void index() throws IOException {
        IndexWriter indexWriter = new IndexWriter(indexDir, new IndexWriterConfig(
                new WhitespaceAnalyzer()).setOpenMode(IndexWriterConfig.OpenMode.CREATE));
        Document doc = new Document();
        doc.add(new SortedSetDocValuesFacetField("Author", "Bob"));
        doc.add(new SortedSetDocValuesFacetField("Publish Year", "2010"));
        indexWriter.addDocument(config.build(doc));

        doc = new Document();
        doc.add(new SortedSetDocValuesFacetField("Author", "Lisa"));
        doc.add(new SortedSetDocValuesFacetField("Publish Year", "2010"));
        indexWriter.addDocument(config.build(doc));

        doc = new Document();
        doc.add(new SortedSetDocValuesFacetField("Author", "Lisa"));
        doc.add(new SortedSetDocValuesFacetField("Publish Year", "2012"));
        indexWriter.addDocument(config.build(doc));

        doc = new Document();
        doc.add(new SortedSetDocValuesFacetField("Author", "Susan"));
        doc.add(new SortedSetDocValuesFacetField("Publish Year", "2012"));
        indexWriter.addDocument(config.build(doc));

        doc = new Document();
        doc.add(new SortedSetDocValuesFacetField("Author", "Frank"));
        doc.add(new SortedSetDocValuesFacetField("Publish Year", "1999"));
        indexWriter.addDocument(config.build(doc));

        indexWriter.close();
    }

    /** User runs a query and counts facets. */
    private List<FacetResult> search() throws IOException {
        DirectoryReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        SortedSetDocValuesReaderState state = new DefaultSortedSetDocValuesReaderState(indexReader);

        // Aggregates the facet counts
        FacetsCollector fc = new FacetsCollector();

        // MatchAllDocsQuery is for "browsing" (counts facets
        // for all non-deleted docs in the index); normally
        // you'd use a "normal" query:
        FacetsCollector.search(searcher, new MatchAllDocsQuery(), 10, fc);

        // Retrieve results
        Facets facets = new SortedSetDocValuesFacetCounts(state, fc);

        List<FacetResult> results = new ArrayList<>();
        results.add(facets.getTopChildren(10, "Author"));
        results.add(facets.getTopChildren(10, "Publish Year"));
        indexReader.close();

        return results;
    }

    /** User drills down on 'Publish Year/2010'. */
    private FacetResult drillDown() throws IOException {
        DirectoryReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        SortedSetDocValuesReaderState state = new DefaultSortedSetDocValuesReaderState(indexReader);

        // Now user drills down on Publish Year/2010:
        DrillDownQuery q = new DrillDownQuery(config);
        q.add("Publish Year", "2010");
        FacetsCollector fc = new FacetsCollector();
        FacetsCollector.search(searcher, q, 10, fc);

        // Retrieve results
        Facets facets = new SortedSetDocValuesFacetCounts(state, fc);
        FacetResult result = facets.getTopChildren(10, "Author");
        indexReader.close();

        return result;
    }

    private List<FacetResult> drillSideways() throws IOException {
        DirectoryReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        SortedSetDocValuesReaderState state = new DefaultSortedSetDocValuesReaderState(indexReader);

        // Passing no baseQuery means we drill down on all
        // documents ("browse only"):
        DrillDownQuery q = new DrillDownQuery(config);

        // Now user drills down on Publish Year/2010:
        q.add("Publish Year", "2010");

        DrillSideways ds = new DrillSideways(searcher, config, state);
        DrillSideways.DrillSidewaysResult result = ds.search(q, 10);

        // Retrieve results
        List<FacetResult> facets = result.facets.getAllDims(10);

        indexReader.close();

        return facets;
    }

    /** Runs the drill-down example. */
    public FacetResult runDrillDown() throws IOException {
        index();
        return drillDown();
    }

    /** Runs the search example. */
    public List<FacetResult> runSearch() throws IOException {
        index();
        return search();
    }

    /** Runs the drill-sideways example. */
    public List<FacetResult> runDrillSideways() throws IOException {
        index();
        return drillSideways();
    }

    /** Runs the search and drill-down examples and prints the results. */
    public static void main(String[] args) throws Exception {
        SimpleSortedSetFacetsExample example = new SimpleSortedSetFacetsExample();

        System.out.println("Facet counting example:");
        System.out.println("-----------------------");
        List<FacetResult> results = example.runSearch();
        System.out.println("Author: " + results.get(0));
        System.out.println("Publish Year: " + results.get(1));

        System.out.println("\n");
        System.out.println("Facet drill-down example (Publish Year/2010):");
        System.out.println("---------------------------------------------");
        System.out.println("Author: " + example.runDrillDown());

        System.out.println("\n");
        System.out.println("Facet drill-sideways example (Publish Year/2010):");
        System.out.println("---------------------------------------------");
        for(FacetResult result : example.runDrillSideways()) {
            System.out.println(result);
        }
    }
}
