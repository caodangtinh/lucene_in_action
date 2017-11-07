package com.tinhcao.lucene.phonetic;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LetterTokenizer;
import org.apache.lucene.analysis.TokenStream;

public class MetaPhoneAnalyzer extends Analyzer {

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new MetaphoneFilter(new LetterTokenizer(reader));
	}

}
