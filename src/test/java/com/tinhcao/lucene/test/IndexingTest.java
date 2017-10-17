package com.tinhcao.lucene.test;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import junit.framework.TestCase;

public class IndexingTest extends TestCase {
	protected String[] ids = { "1", "2" };
	protected String[] unindexed = { "Netherlands", "Italy" };
	protected String[] unstored = { "Amsterdam has lot of bridges", "Venice has lots of canals" };
	protected String[] text = { "Amsterdam", "Venice" };
	private Directory directory;

	@Override
	protected void setUp() throws Exception {
		directory = new RAMDirectory();
		IndexWriter indexWriter = getWriter();
		for (int i = 0; i < ids.length; i++) {
			Document document = new Document();
			document.add(new Field("id", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
			document.add(new Field("country", unindexed[i], Field.Store.YES, Field.Index.NO));
			document.add(new Field("contents", unstored[i], Field.Store.NO, Field.Index.ANALYZED));
			document.add(new Field("city", text[i], Field.Store.YES, Field.Index.ANALYZED));
			indexWriter.addDocument(document);
		}
		indexWriter.close();
	}

	private IndexWriter getWriter() throws IOException {
		return new IndexWriter(directory, new StandardAnalyzer(Version.LUCENE_30),
				IndexWriter.MaxFieldLength.UNLIMITED);
	}

	protected int getHitCount(String fieldName, String searchString) throws IOException {
		IndexSearcher indexSearcher = new IndexSearcher(directory);
		Query query = new TermQuery(new Term(fieldName, searchString));
		int hitCount = TestUtil.hitCount(indexSearcher, query);
		indexSearcher.close();
		return hitCount;
	}

	@Test
	public void testIndexWriter() throws IOException {
		IndexWriter indexWriter = getWriter();
		assertEquals(ids.length, indexWriter.numDocs());
		indexWriter.close();
	}
}
