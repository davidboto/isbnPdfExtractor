package com.aquarelah.isbnPdfExtractor;

public class SimpleParser implements Parser {
	  
	public String find(String text, String searchString, String searchString2) {
		String [] documentLines = text.split("\r\n|\r|\n");
		for (int i = 0; i < documentLines.length; i++) {
			if (documentLines[i].contains(searchString) && documentLines[i].contains(searchString2)) {
				return documentLines[i];
			}
		}
		return "";
	}

}
