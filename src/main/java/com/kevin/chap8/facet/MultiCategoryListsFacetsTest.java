package com.kevin.chap8.facet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
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

public class MultiCategoryListsFacetsTest {
	private final Directory indexDir = FSDirectory.open(Paths.get("D:/lucene/index"));
	private final Directory taxoDir = FSDirectory.open(Paths.get("D:/lucene/facet"));
	private final FacetsConfig config = new FacetsConfig();

	public MultiCategoryListsFacetsTest() throws IOException {
	    // 设置维度对应的索引域名，默认的索引域名是$facets
		config.setIndexFieldName("Author", "author");
		config.setIndexFieldName("Publish Date", "pubdate");
		// 设置多层次维度
		config.setHierarchical("Publish Date", true);
	}

	/**
	 * 创建测试索引
	 * @throws IOException
	 */
	private void index() throws IOException {
		IndexWriter indexWriter = new IndexWriter(this.indexDir,
				new IndexWriterConfig(new WhitespaceAnalyzer())
						.setOpenMode(IndexWriterConfig.OpenMode.CREATE));

		DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(
				this.taxoDir, IndexWriterConfig.OpenMode.CREATE);

		Document doc = new Document();
		doc.add(new FacetField("Author", new String[] { "Bob" }));
		doc.add(new FacetField("Publish Date", new String[] { "2010", "10", "15" }));
		indexWriter.addDocument(this.config.build(taxoWriter, doc));

		doc = new Document();
		doc.add(new FacetField("Author", new String[] { "Lisa" }));
		doc.add(new FacetField("Publish Date", new String[] { "2010", "10", "20" }));
		indexWriter.addDocument(this.config.build(taxoWriter, doc));

		doc = new Document();
		doc.add(new FacetField("Author", new String[] { "Lisa" }));
		doc.add(new FacetField("Publish Date", new String[] { "2012", "1", "1" }));
		indexWriter.addDocument(this.config.build(taxoWriter, doc));

		doc = new Document();
		doc.add(new FacetField("Author", new String[] { "Susan" }));
		doc.add(new FacetField("Publish Date", new String[] { "2012", "1", "7" }));
		indexWriter.addDocument(this.config.build(taxoWriter, doc));

		doc = new Document();
		doc.add(new FacetField("Author", new String[] { "Frank" }));
		doc.add(new FacetField("Publish Date", new String[] { "1999", "5", "5" }));
		indexWriter.addDocument(this.config.build(taxoWriter, doc));

		indexWriter.close();
		taxoWriter.close();
	}

	private List<FacetResult> search() throws IOException {
		DirectoryReader indexReader = DirectoryReader.open(this.indexDir);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		TaxonomyReader taxoReader = new DirectoryTaxonomyReader(this.taxoDir);
		FacetsCollector fc = new FacetsCollector();
		FacetsCollector.search(searcher, new MatchAllDocsQuery(), 10, fc);
		List<FacetResult> results = new ArrayList<>();
		Facets author = new FastTaxonomyFacetCounts("author", taxoReader, config, fc);
		results.add(author.getTopChildren(10, "Author", new String[0]));
		Facets pubDate = new FastTaxonomyFacetCounts("pubdate", taxoReader, config, fc);
		results.add(pubDate.getTopChildren(10, "Publish Date", new String[0]));
		indexReader.close();
		taxoReader.close();
		return results;
	}

	public List<FacetResult> runSearch() throws IOException {
		index();
		return search();
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Facet counting over multiple category lists example:");
		System.out.println("-----------------------");
		List<FacetResult> results = new MultiCategoryListsFacetsTest().runSearch();
		System.out.println("Author: " + results.get(0));
		System.out.println("Publish Date: " + results.get(1));
	}
}
