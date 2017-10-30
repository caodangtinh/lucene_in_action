package com.tinhcao.lucene.test;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
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

}
