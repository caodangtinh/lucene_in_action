package com.tinhcao.lucene.test;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import com.tinhcao.lucene.phonetic.MetaPhoneAnalyzer;

import junit.framework.TestCase;

public class TestAnalyzer extends TestCase {

	@Test
	public void testKoolKat() throws Exception {
		Directory directory = new RAMDirectory();
		MetaPhoneAnalyzer analyzer = new MetaPhoneAnalyzer();
		IndexWriter indexWriter = new IndexWriter(directory, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);
		Document document = new Document();
		document.add(new Field("contents", "cool cat", Field.Store.YES, Field.Index.ANALYZED));
		indexWriter.addDocument(document);
		indexWriter.close();

		// searching
		IndexSearcher indexSearcher = new IndexSearcher(directory);
		Query query = new QueryParser(Version.LUCENE_30, "contents", analyzer).parse("kool cat");
		TopDocs topDocs = indexSearcher.search(query, 1);
		assertEquals(1, topDocs.totalHits);

		int docId = topDocs.scoreDocs[0].doc;
		document = indexSearcher.doc(docId);
		assertEquals("cool cat", document.get("contents"));

		indexSearcher.close();
	}

}
