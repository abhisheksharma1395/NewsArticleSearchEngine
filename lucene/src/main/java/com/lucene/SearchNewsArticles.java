package com.lucene;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

public class SearchNewsArticles {

	public static void main(String[] args) throws IOException, ParseException {
		
		String jsonFilesPath = "C:\\IRProject\\data\\";
		String indexPath = "C:\\IRProject\\Index";
		String queryResultStorePath = "C:\\IRProject\\IndexData\\";
		String query = "vaccine";
		int numberOfArticlesToFetch = 5;
		
		LuceneIndexWriter index = new LuceneIndexWriter(jsonFilesPath, indexPath);
		index.createIndex();

		LuceneIndexReader reader = new LuceneIndexReader(queryResultStorePath, indexPath);
		reader.search(query,numberOfArticlesToFetch);
		
//		if(args[5].equals("true")) {
//			
//			LuceneIndexWriter index = new LuceneIndexWriter(args[0], args[1]);
//			index.createIndex();
//		}
//
//		LuceneIndexReader reader = new LuceneIndexReader(args[2], args[1]);
//		int numberOfTopArticles = Integer.parseInt(args[4]);
//		reader.search(args[3],numberOfTopArticles);
		
	}

}
