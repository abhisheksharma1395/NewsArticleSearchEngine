package com.lucene;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.FileUtils;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class LuceneIndexWriter {

	private String indexPath = null;
	private String jsonFilePath = null;

	public LuceneIndexWriter(String jsonFilePath, String indexPath) {
		super();
		this.indexPath = indexPath;
		this.jsonFilePath = jsonFilePath;
	}

	public void createIndex() throws IOException {
		
		File dir = new File(indexPath);
		FileUtils.cleanDirectory(dir);
		File file = new File(jsonFilePath);
		File[] files = file.listFiles();
		IndexWriter indexWriter = openIndex();
		for (File f : files) {
			JSONArray jsonObjects = parseJSONFile(f.getName());
			addDocuments(jsonObjects, indexWriter);
		}

		closeIndex(indexWriter);
	}

	private IndexWriter openIndex() {
		IndexWriter indexWriter = null;
		try {

			Path path = Paths.get(indexPath);
			Directory directory = FSDirectory.open(path);
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			indexWriter = new IndexWriter(directory, config);

		} catch (Exception e) {
			System.err.println("Error opening the index. " + e.getMessage());
		}

		return indexWriter;
	}

	private JSONArray parseJSONFile(String filename) throws FileNotFoundException {

		InputStream jsonFile = new FileInputStream(jsonFilePath + filename);
		Reader readerJson = new InputStreamReader(jsonFile);
		Object fileObjects = JSONValue.parse(readerJson);
		JSONArray arrayObjects = (JSONArray) fileObjects;
		return arrayObjects;
	}

	private void addDocuments(JSONArray jsonObjects, IndexWriter indexWriter) throws IOException {

		for (JSONObject object : (List<JSONObject>) jsonObjects) {
			Document doc = new Document();
			
			FieldType URLOptions = new FieldType();
			URLOptions.setIndexOptions(IndexOptions.NONE);
			URLOptions.setStored(true);
			
			doc.add(new TextField("articleBody", (String) object.get("articleBody"), Field.Store.YES));
			doc.add(new TextField("articleTitle", (String) object.get("articleTitle"), Field.Store.YES));
			doc.add(new Field("articleUrl", (String) object.get("articleUrl"), URLOptions));
			indexWriter.addDocument(doc);
			//String term = String.valueOf(object.get("articleID"));
			//indexWriter.updateDocument(new Term(term), doc);
		}

	}

	private void closeIndex(IndexWriter indexWriter) throws IOException {

		indexWriter.commit();
		indexWriter.close();

	}

}
