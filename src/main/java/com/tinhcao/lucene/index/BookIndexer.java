package com.tinhcao.lucene.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.tinhcao.model.Book;

public class BookIndexer {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length != 2) {
			throw new IllegalArgumentException("Using format <data dir> <index dir>");
		}
		String dataDir = args[0];
		String indexDir = args[1];
		//
		BookIndexer bookIndexer = new BookIndexer();
		Set<Book> books = new HashSet<>();
		Directory directory = FSDirectory.open(new File(indexDir));
		IndexWriter indexWriter = new IndexWriter(directory, new StandardAnalyzer(Version.LUCENE_30), true,
				IndexWriter.MaxFieldLength.UNLIMITED);
		indexWriter.setInfoStream(System.out);
		//
		bookIndexer.getListBookFromFile(dataDir, books);
		for (Book book : books) {
			indexWriter.addDocument(bookIndexer.buildDocument(book));
		}
		indexWriter.close();
		directory.close();

	}

	public Document buildDocument(Book book) {
		Document document = new Document();
		document.add(new Field("title", book.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
		document.add(new Field("isbn", book.getTitle(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		document.add(new Field("author", book.getTitle(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		document.add(new NumericField("pubMonth", Field.Store.YES, true).setIntValue(book.getPubMonth()));
		document.add(new Field("subject", book.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
		document.add(new Field("url", book.getTitle(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		return document;
	}

	public void getListBookFromFile(String dataDir, Set<Book> books) throws FileNotFoundException, IOException {
		File dataDirectory = new File(dataDir);
		File[] listFiles = dataDirectory.listFiles();
		for (File f : listFiles) {
			if (f.isDirectory()) {
				getListBookFromFile(f.getAbsolutePath(), books);
			} else {
				books.add(getBookFromFile(f));
			}
		}
	}

	private Book getBookFromFile(File bookPropFile) throws FileNotFoundException, IOException {
		Book book = new Book();
		Properties properties = new Properties();
		properties.load(new FileInputStream(bookPropFile));
		book.setAuthor(properties.getProperty("author"));
		book.setIsbn(properties.getProperty("isbn"));
		book.setPubMonth(Integer.parseInt(properties.getProperty("pubmonth")));
		book.setSubject(properties.getProperty("subject"));
		book.setTitle(properties.getProperty("title"));
		book.setUrl(properties.getProperty("url"));
		return book;
	}

}
