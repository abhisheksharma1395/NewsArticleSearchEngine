package com.index.lucene;

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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LuceneIndexReader {

    DirectoryReader indexReader = null;
    Directory directory = null;
    int articlesToFetch = 10;
    private String indexPath = null;

    public LuceneIndexReader(String indexPath) {
        super();
        this.indexPath = indexPath;
    }

    public JSONArray search(String query, int skip) throws ParseException, IOException {

        QueryParser parser = createQueryParser();
        Query parsedQuery = parser.parse(query);
        IndexSearcher indexSearcher = getIndexSearcher();

        JSONArray articles = (JSONArray) getTopArticles(skip, parsedQuery, indexSearcher);

        finish();
        return articles;
    }


    private JSONArray getTopArticles(int skip, Query parsedQuery, IndexSearcher indexSearcher) throws IOException {

        ScoreDoc[] hits = indexSearcher.search(parsedQuery, skip + this.articlesToFetch).scoreDocs;

        JSONArray jsonArray = new JSONArray();
        for (int rank = skip; rank < hits.length; ++rank) {

            Document hitDoc = indexSearcher.doc(hits[rank].doc);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("rank", rank + 1);
            jsonObject.put("score", hits[rank].score);
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

