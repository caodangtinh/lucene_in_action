package com.tinhcao.lucene.test;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import junit.framework.TestCase;

public class BasicSearchingTest extends TestCase {
	private static final String INDEX_DIR = "C:\\index";
	@Test
	public void testTerm() throws IOException, ParseException {
		Directory directory = TestUtil.getBookIndexDirectory(INDEX_DIR);
		IndexSearcher indexSearcher = new IndexSearcher(directory);
		QueryParser parser = new QueryParser(Version.LUCENE_30, "subject", new StandardAnalyzer(Version.LUCENE_30));
		Query query = parser.parse("ant");
		TopDocs docs = indexSearcher.search(query, 10);
		assertEquals("Ant in Action", 1, docs.totalHits);
		query = parser.parse("JUnit");
		docs = indexSearcher.search(query, 10);
		assertEquals("Ant in action, "
				+ "JUnit in Action", 1, docs.totalHits);
		indexSearcher.close();
		directory.close();

	}
	
	@Test
	public void testTermRangeQuery() throws IOException {
		Directory directory = TestUtil.getBookIndexDirectory(INDEX_DIR);
		IndexSearcher indexSearcher = new IndexSearcher(directory);
		Query query = new TermRangeQuery("title", "a", "n", true, true);
		TopDocs topDocs = indexSearcher.search(query, 100);
		assertEquals(12, topDocs.totalHits);
		indexSearcher.close();
		indexSearcher.close();
	}
	
	@Test
	public void testNumericRangeQuery() throws IOException {
		Directory directory = TestUtil.getBookIndexDirectory(INDEX_DIR);
		IndexSearcher indexSearcher = new IndexSearcher(directory);
		NumericRangeQuery<Integer> numericRangeQuery = NumericRangeQuery.newIntRange("pubMonth", 200403, 200611, true, true);
		TopDocs topDocs = indexSearcher.search(numericRangeQuery, 10);
		assertEquals(5, topDocs.totalHits);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			TestUtil.display(indexSearcher.doc(scoreDoc.doc));
		}
		indexSearcher.close();
		indexSearcher.close();
	}
	

}
