package com.aquarelah.isbnPdfExtractor;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFToText {
	
	private PDDocument document;

	private File input;
	
	public PDFToText(String filePath) throws IOException {
		input = new File(filePath); 
		document = PDDocument.load(input);
	}
	
	public PDFToText(String filePath, String password) throws IOException {
		input = new File(filePath); 
		document = PDDocument.load(input, password);
	}
	
	public String getText() throws IOException {
		PDFTextStripper stripper = new PDFTextStripper();
		int lastPage = document.getNumberOfPages();
		stripper.setStartPage(1);
		stripper.setEndPage(lastPage); 
		String result = stripper.getText(document);		
		return result;
	}

	public PDDocument getDocument() {
		return document;
	}

	public void setDocument(PDDocument document) {
		this.document = document;
	}

	public File getInput() {
		return input;
	}

	public void setInput(File input) {
		this.input = input;
	}
}
