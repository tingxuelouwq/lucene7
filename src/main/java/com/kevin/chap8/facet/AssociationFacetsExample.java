package com.kevin.chap8.facet;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.*;
import org.apache.lucene.facet.taxonomy.*;
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
 * 类名: AssociationFacetsExample<br/>
 * 包名：com.kevin.chap8.facet<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/20 15:04<br/>
 * 版本：1.0<br/>
 * 描述：Show example usage of category associations.<br/>
 */
public class AssociationFacetsExample {

    private final Directory indexDir = FSDirectory.open(Paths.get("D:/lucene/index"));
    private final Directory taxoDir = FSDirectory.open(Paths.get("D:/lucene/facet"));
    private final FacetsConfig config = new FacetsConfig();

    public AssociationFacetsExample() throws IOException {
        config.setMultiValued("tags", true);
        config.setIndexFieldName("tags", "$tags");
        config.setMultiValued("genre", true);
        config.setIndexFieldName("genre", "$genre");
    }

    /** Build the example index. */
    private void index() throws IOException {
        IndexWriter indexWriter = new IndexWriter(indexDir, new IndexWriterConfig(
                new WhitespaceAnalyzer()).setOpenMode(IndexWriterConfig.OpenMode.CREATE));

        // Writes facet ords to a separate directory from the main index
        DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir,
                IndexWriterConfig.OpenMode.CREATE);

        Document doc = new Document();
        // 3 occurrences for tag 'lucene'
        doc.add(new IntAssociationFacetField(3, "tags", "lucene"));
        // 87% confidence level of genre 'computing'
        doc.add(new FloatAssociationFacetField(0.87f, "genre", "computing"));
        indexWriter.addDocument(config.build(taxoWriter, doc));

        doc = new Document();
        // 1 occurrence for tag 'lucene'
        doc.add(new IntAssociationFacetField(1, "tags", "lucene"));
        // 2 occurrence for tag 'solr'
        doc.add(new IntAssociationFacetField(2, "tags", "solr"));
        // 75% confidence level of genre 'computing'
        doc.add(new FloatAssociationFacetField(0.75f, "genre", "computing"));
        // 34% confidence level of genre 'software'
        doc.add(new FloatAssociationFacetField(0.34f, "genre", "software"));
        indexWriter.addDocument(config.build(taxoWriter, doc));

        indexWriter.close();
        taxoWriter.close();
    }

    /** User runs a query and aggregates facets by summing their association values. */
    private List<FacetResult> sumAssociations() throws IOException {
        DirectoryReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

        FacetsCollector fc = new FacetsCollector();

        // MatchAllDocsQuery is for "browsing" (counts facets
        // for all non-deleted docs in the index); normally
        // you'd use a "normal" query:
        FacetsCollector.search(searcher, new MatchAllDocsQuery(), 10, fc);

        Facets tags = new TaxonomyFacetSumIntAssociations("$tags", taxoReader, config, fc);
        Facets genre = new TaxonomyFacetSumFloatAssociations("$genre", taxoReader, config, fc);

        // Retrieve results
        List<FacetResult> results = new ArrayList<>();
        results.add(tags.getTopChildren(10, "tags"));
        results.add(genre.getTopChildren(10, "genre"));

        indexReader.close();
        taxoReader.close();

        return results;
    }

    /** User drills down on 'tags/solr'. */
    private FacetResult drillDown() throws IOException {
        DirectoryReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

        // Passing no baseQuery means we drill down on all
        // documents ("browse only"):
        DrillDownQuery q = new DrillDownQuery(config);

        // Now user drills down on Publish Date/2010:
        q.add("tags", "solr");
        FacetsCollector fc = new FacetsCollector();
        FacetsCollector.search(searcher, q, 10, fc);

        // Retrieve results
        Facets facets = new TaxonomyFacetSumFloatAssociations("$genre", taxoReader, config, fc);
        FacetResult result = facets.getTopChildren(10, "genre");

        indexReader.close();
        taxoReader.close();

        return result;
    }

    /** Runs summing association example. */
    public List<FacetResult> runSumAssociations() throws IOException {
        index();
        return sumAssociations();
    }

    /** Runs the drill-down example. */
    public FacetResult runDrillDown() throws IOException {
        index();
        return drillDown();
    }

    /** Runs the sum int/float associations examples and prints the results. */
    public static void main(String[] args) throws Exception {
        AssociationFacetsExample example = new AssociationFacetsExample();

        System.out.println("Sum associations example:");
        System.out.println("-------------------------");
        List<FacetResult> results = example.runSumAssociations();
        System.out.println("tags: " + results.get(0));
        System.out.println("genre: " + results.get(1));

        System.out.println("\n");
        System.out.println("Sum associations drill-down example (tags/solr):");
        System.out.println("---------------------------------------------");
        System.out.println("genre: " + example.runDrillDown());
    }
}
