package com.tinhcao.lucene.phonetic;

import java.io.IOException;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public class MetaphoneFilter extends TokenFilter {
	public static final String METAPHONE = "metaphone";
	private DoubleMetaphone metaphoner = new DoubleMetaphone();
	private TermAttribute termAttribute;
	private TypeAttribute typeAttribute;

	protected MetaphoneFilter(TokenStream input) {
		super(input);
		termAttribute = addAttribute(TermAttribute.class);
		typeAttribute = addAttribute(TypeAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (!input.incrementToken())
			return false;
		String encoded = metaphoner.encode(termAttribute.term());
		termAttribute.setTermBuffer(encoded);
		typeAttribute.setType(METAPHONE);
		return true;
	}

}
