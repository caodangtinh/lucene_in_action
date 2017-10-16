package com.tinhcao.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
	private IndexWriter writer;

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new IllegalArgumentException("Usage: java " + Indexer.class.getName() + " <index dir> <data dir>");
		}
		String indexDir = args[0];
		String dataDir = args[1];
		long start = System.currentTimeMillis();
		Indexer indexer = new Indexer(indexDir);
		int numIndexed;
		try {
			numIndexed = indexer.index(dataDir, new TextFilesFilter());
		} finally {
			indexer.close();
		}
		long end = System.currentTimeMillis();
		System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " miliseconds");

	}

	public Indexer(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));
		writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30), true,
				IndexWriter.MaxFieldLength.UNLIMITED);
	}

	public void close() throws IOException {
		writer.close();
	}

	public int index(String dataDir, FileFilter filter) throws Exception {
		File[] files = new File(dataDir).listFiles();
		for (File f : files) {
			if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead()
					&& (filter == null || filter.accept(f))) {
				indexFile(f);
			}
		}
		return writer.numDocs();
	}

	private static class TextFilesFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().toLowerCase().endsWith(".txt");
		}
	}

	protected Document buildDocument(File f) throws IOException {
		Document document = new Document();
		document.add(new Field("contents", new FileReader(f)));
		document.add(new Field("filename", f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		document.add(new Field("fullpath", f.getCanonicalPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		return document;
	}

	private void indexFile(File f) throws IOException {
		System.out.println("Indexing " + f.getCanonicalPath());
		Document doc = buildDocument(f);
		writer.addDocument(doc);
	}
}
