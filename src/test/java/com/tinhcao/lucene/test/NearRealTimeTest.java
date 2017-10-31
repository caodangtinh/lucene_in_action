package com.tinhcao.lucene.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class NearRealTimeTest {

	private IndexSearcher indexSearcher;

	@Test
	public void testNearRealTime() throws CorruptIndexException, LockObtainFailedException, IOException, ParseException {
		Directory directory = new RAMDirectory();
		
		// indexing part
		IndexWriter indexWriter = new IndexWriter(directory, new StandardAnalyzer(Version.LUCENE_30),
				IndexWriter.MaxFieldLength.UNLIMITED);
		for (int i = 0; i < 10; i++) {
			Document document = new Document();
			document.add(new Field("id", ""+i, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
			document.add(new Field("text", "aaa", Field.Store.YES, Field.Index.ANALYZED));
			indexWriter.addDocument(document);
		}
		// searching part
		IndexReader indexReader = indexWriter.getReader();
		indexSearcher = new IndexSearcher(indexReader);
		QueryParser queryParser = new QueryParser(Version.LUCENE_30, "text", new StandardAnalyzer(Version.LUCENE_30));
		Query query = queryParser.parse("aaa");
		TopDocs topDocs = indexSearcher.search(query, 1);
		indexSearcher.close();
		assertEquals(10, topDocs.totalHits);
		
		// delete document with id = 7
		indexWriter.deleteDocuments(new TermQuery(new Term("id", "7")));
		
		// add new document with id = 11
		Document document =new Document();
		document.add(new Field("id", ""+11, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
		document.add(new Field("text", "bbb", Field.Store.YES, Field.Index.ANALYZED));
		indexWriter.addDocument(document);
		// create index reader
		IndexReader indexReader2 = indexReader.reopen();
		assertFalse(indexReader == indexReader2);
		indexReader.close();
		indexSearcher = new IndexSearcher(indexReader2);
		// confirm hit 9 with old data
		TopDocs topDocs2 = indexSearcher.search(query, 10);
		assertEquals(9, topDocs2.totalHits);
		for (ScoreDoc scoreDoc : topDocs2.scoreDocs) {
			System.out.println(indexReader2.document(scoreDoc.doc).get("id") + " - " + indexReader2.document(scoreDoc.doc).get("text") + " : " + scoreDoc.score);
		}
		// confirm hit 1 with new data
		TopDocs topDocs3 = indexSearcher.search(new TermQuery(new Term("text", "bbb")), 1);
		assertEquals(1, topDocs3.totalHits);
		for (ScoreDoc scoreDoc : topDocs3.scoreDocs) {
			Explanation explanation = indexSearcher.explain(new TermQuery(new Term("text", "bbb")), scoreDoc.doc);
			System.out.println(explanation.toString());
		}
		indexWriter.close();
	}

}
