package com.lucene;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LuceneIndexReader {

	private String indexPath = null;
	private String jsonFilePath = null;
	DirectoryReader indexReader = null;
	Directory directory = null;

	public LuceneIndexReader(String jsonFilePath, String indexPath) {
		super();
		this.indexPath = indexPath;
		this.jsonFilePath = jsonFilePath;
	}

	public void search(String query, int numberOfTopDocument) throws ParseException, IOException {

		QueryParser parser = createQueryParser();
		Query parsedQuery = parser.parse(query);
		IndexSearcher indexSearcher = getIndexSearcher();

		JSONArray jsonArray = (JSONArray) getTopArticles(numberOfTopDocument, parsedQuery, indexSearcher);

		saveTopArticlesInFile(jsonArray);
		printTopArticles(jsonArray);
		
		finish();
	}

	private void printTopArticles(JSONArray jsonArray) {
		for(int i=0; i < jsonArray.size(); i++) {
	         System.out.println(jsonArray.get(i).toString());
	    }
	}

	private void saveTopArticlesInFile(JSONArray jsonArray) throws IOException {
		FileWriter file = new FileWriter(jsonFilePath + "TopAricles.json");
		jsonArray.writeJSONString(file);
		file.flush();
		file.close();
	}

	private JSONArray getTopArticles(int numberOfTopDocument, Query parsedQuery, IndexSearcher indexSearcher)
			throws IOException {

		int topHitCount = numberOfTopDocument;
		ScoreDoc[] hits = indexSearcher.search(parsedQuery, topHitCount).scoreDocs;

		JSONArray jsonArray = new JSONArray();
		for (int rank = 0; rank < hits.length; ++rank) {

			Document hitDoc = indexSearcher.doc(hits[rank].doc);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("Rank", rank + 1);
			jsonObject.put("Score", hits[rank].score);
			jsonObject.put("articleTitle", hitDoc.get("articleTitle"));
			jsonObject.put("articleBody", hitDoc.get("articleBody"));
			jsonObject.put("articleUrl", hitDoc.get("articleUrl"));
			jsonArray.add(jsonObject);
		}

		return jsonArray;
	}

	
	private QueryParser createQueryParser() throws IOException {

		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("articleBody", analyzer);
		return parser;
	}

	private IndexSearcher getIndexSearcher() throws IOException {

		Path path = Paths.get(indexPath);
		directory = FSDirectory.open(path);
		indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		return indexSearcher;
	}

	private void finish() throws IOException {

		indexReader.close();
		directory.close();

	}

}
