package com.aquarelah.isbnPdfExtractor;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class Client {

	public final static String BASE_URL_FORMAT = "https://openlibrary.org/api/books?bibkeys=ISBN:%s&jscmd=data&format=json";
	public final static int WAIT_INTERVAL_BETWEEN_REQUEST = 1000;
	public final static int ISBN13_SIZE = 13;

	private String filesDirectoryPath;

	public Client(String filesDirectoryPath) {
		this.filesDirectoryPath = filesDirectoryPath;
	}
	

	public void execute() throws IOException, InterruptedException, ExecutionException {
		java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);

		List<String> fileList = getFiles(filesDirectoryPath, ".pdf");

		List<BookInfo> books = getISBNFromFiles(fileList);

		searchAndUpdate(books);
		
		printBooksInfo(books);
				
		if(yesNoAnswer("Rename Files?")) {
			renameFiles(books);	
		}
	
	}
	
	private static void renameFiles(List<BookInfo> books) {
		for (BookInfo book : books) {
			if (!book.getIsbn().isBlank() && !book.getTitle().isBlank()) {
				Path source = Paths.get(book.getFilepath());
				Path target = Paths.get(findFilename(book.getFilepath(), sanitize(book.getTitle()) + ".pdf"));
				try {
					Files.move(source, target);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static boolean yesNoAnswer(String message) {
		Scanner scanner = new Scanner(System.in);
		System.out.println(message + " [yes/no]");
		boolean answer = false;
		String read;
		do {
			read = scanner.next();
			if(read.contains("yes")) {
				answer = true;
			}
			if(read.contains("no")) {
				answer = false;
			}
		} while(!read.contains("yes") && !read.contains("no"));
		return answer;
	}
	
	
	private static void printBooksInfo(List<BookInfo> books) {
		for (BookInfo book : books) {
			if (!book.getIsbn().isBlank() && !book.getTitle().isBlank()) {
				System.out.println(book.toString());
			}
		}
	}
	

	private static String sanitize(String text) {

		Map<Character, String> mapCharString = new HashMap<Character, String>();
		StringBuffer sanitizedString = new StringBuffer();

		mapCharString.put('.', " ");
		mapCharString.put(':', "-");
		mapCharString.put('\'', "-");
		mapCharString.put('/', "-");

		for (int i = 0; i < text.length(); i++) {
			if (mapCharString.containsKey(text.charAt(i))) {
				sanitizedString.append(mapCharString.get(text.charAt(i)));
			} else {
				sanitizedString.append(text.charAt(i));
			}
		}

		return sanitizedString.toString();
	}
	

	private static String findFilename(String path, String filename) {
		int slashPosition = 0;
		for (int i = path.length() - 1; i > 0; i--) {
			Character c = path.charAt(i);
			if (c == '/') {
				slashPosition = i;
				break;
			}
		}
		return path.substring(0, slashPosition + 1).concat(filename);
	}

	
	private static void searchAndUpdate(List<BookInfo> books) throws IOException, InterruptedException, ExecutionException {

		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
			Gson gson = new Gson();
			
			for (BookInfo booky : books) {
				
				if (!booky.getIsbn().isBlank()) {
					HttpUriRequest request = new HttpGet(String.format(BASE_URL_FORMAT, booky.getIsbn()));
					CloseableHttpResponse response = httpclient.execute(request);
					
					if (response.getEntity() != null && response.getStatusLine().getStatusCode() == 200) {
						String res = EntityUtils.toString(response.getEntity());

						if (!res.isBlank() && !res.contentEquals("{}")) {
							JsonObject convertedObject = new Gson().fromJson(res, JsonObject.class);

							Iterator<String> convertedObjectIt = convertedObject.keySet().iterator();
							String nomeObjeto = convertedObjectIt.next();
							BookInfo book = gson.fromJson(convertedObject.getAsJsonObject(nomeObjeto), BookInfo.class);

							for (BookInfo bookStore : books) {
								if (bookStore.getIsbn().contentEquals(nomeObjeto.split(":")[1])) {
									bookStore.setTitle(book.getTitle());
								}
							}
						}
					}
				}
			}
		} finally {
			httpclient.close();
		}
	}

	
	private static List<String> getFiles(String dir, String fileExtension) throws IOException {
		Stream<Path> walk = Files.walk(Paths.get(dir), FileVisitOption.FOLLOW_LINKS);
		List<String> fileList = walk.map(x -> x.toString()).filter(f -> f.endsWith(fileExtension))
				.collect(Collectors.toList());
		walk.close();
		return fileList;
	}

	
	private static List<BookInfo> getISBNFromFiles(List<String> filesList) throws IOException {
		Parser parser = new SimpleParser();
		List<BookInfo> books = new ArrayList<>();
		for (String filepath : filesList) {
			PDFToText pdf = new PDFToText(filepath);
			String ISBNLine = parser.find(pdf.getText(), "ISBN", "978");
			books.add(new BookInfo("", filepath, ISBN13Extractor(ISBNLine)));
		}
		return books;
	}

	
	private static String ISBN13Extractor(String text) {
		StringBuffer ISBN = new StringBuffer();

		for (int charPosition = 0; charPosition < text.length(); charPosition++) {
			int digitsFound = 0;
			if (text.charAt(charPosition) == '9' && text.charAt(charPosition + 1) == '7'
					&& text.charAt(charPosition + 2) == '8') {
				ISBN.append("978");
				digitsFound = ISBN.length();
				charPosition = charPosition + digitsFound;
				while (digitsFound < ISBN13_SIZE && charPosition < text.length()) {
					if (Character.isDigit(text.charAt(charPosition))) {
						ISBN.append(text.charAt(charPosition));
						digitsFound++;
					}
					charPosition++;
				}
				break;
			}
		}

		System.out.println(text + "->" + ISBN.toString());

		return ISBN.toString();
	}
}
