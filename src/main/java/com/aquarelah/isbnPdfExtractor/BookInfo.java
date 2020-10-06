package com.aquarelah.isbnPdfExtractor;

import java.util.List;

public class BookInfo {

	public int id;
	public String title;
	public String filepath;
	public List<String> isbns;
	public String isbn;

	public BookInfo() {
	}
		
	public BookInfo(String title, String filepath, String isbn) {
		super();
		this.title = title;
		this.filepath = filepath;
		this.isbn = isbn;
	}

	public BookInfo(int id, String title, String filepath, String isbn) {
		super();
		this.id = id;
		this.title = title;
		this.filepath = filepath;
		this.isbn = isbn;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public List<String> getIsbns() {
		return isbns;
	}

	public void setIsbns(List<String> isbns) {
		this.isbns = isbns;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	
	public String toString() {
		StringBuffer strBf = new StringBuffer();
		strBf.append("Title: " + this.title + "\n");
		strBf.append("ISBN: " + this.isbn + "\n");
		strBf.append("Filepath: " + this.filepath + "\n");
		return strBf.toString();
	}
	
}
