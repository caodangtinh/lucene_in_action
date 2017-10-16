package com.tinhcao.lucene.search;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
//import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {

	public static void main(String[] args) throws IOException, ParseException {
		if (args.length != 2) {
			throw new IllegalArgumentException("Usage: java " + Searcher.class.getName() + " <index dir> <query>");
		}
		String indexDir = args[0];
		String query = args[1];
		search(indexDir, query);
	}

	public static void search(String indexDir, String query) throws IOException, ParseException {
		Directory directory = FSDirectory.open(new File(indexDir));
		IndexSearcher indexSearcher = new IndexSearcher(directory);
		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new StandardAnalyzer(Version.LUCENE_30));
		Query q = parser.parse(query);
		// using Term Query
//		Query q = new TermQuery(new Term("contents", query));
		long start = System.currentTimeMillis();
		TopDocs topDocs = indexSearcher.search(q, 10);
		long end = System.currentTimeMillis();
		System.out.println("Found " + topDocs.totalHits + " document (s) in " + (end - start)
				+ " miliseconds that match query: " + query);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document document = indexSearcher.doc(scoreDoc.doc);
			System.out.println(document.get("fullpath"));
		}
		indexSearcher.close();
	}

}
