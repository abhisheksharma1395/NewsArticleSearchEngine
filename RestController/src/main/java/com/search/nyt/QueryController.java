package com.search.nyt;

import com.index.hadoop.Articles;
import com.index.hadoop.GetArticles;
import com.index.hadoop.ParseReducerOutput;
import com.index.hadoop.WordIndex;
import com.index.lucene.LuceneIndexReader;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.queryparser.classic.ParseException;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@RestController
public class QueryController {

    private final MongoTemplate mongoTemplate;

    @Value("${index.lucene.path}")
    String luceneIndexPath;

    @Value("${index.hadoop.path}")
    String hadoopIndexPath;

    public QueryController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/lucene/fetch")
    public JSONArray getArticlesUsingLucene(@RequestParam(name = "query") String query, @RequestParam(name = "skip") int skip) throws ParseException, IOException {


        LuceneIndexReader reader = new LuceneIndexReader(luceneIndexPath);
        JSONArray articles = reader.search(query, skip);

        return articles;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/hadoop/fetch")
    public List<Articles> getArticlesUsingHadoop(@RequestParam(name = "query") String query, @RequestParam(name = "skip") int skip) throws ParseException, IOException {


        List<String> words = getTokenizedWords(query);

        Query indexQuery = new Query();
        indexQuery.addCriteria(Criteria.where("word").in(words));
        List<WordIndex> wordIndex = mongoTemplate.find(indexQuery, WordIndex.class);
        List<Articles> articles = new ArrayList<>();

        if(wordIndex.isEmpty() || wordIndex.size() == 0)
            return articles;

        List<Integer> articlesIds = new GetArticles(wordIndex).getArticlesId(skip);

        Query articleFetchQuery = new Query();
        articleFetchQuery.addCriteria(Criteria.where("articleID").in(articlesIds));
        articles = mongoTemplate.find(articleFetchQuery, Articles.class);

        return articles;
    }


    private List<String> getTokenizedWords(String query) throws IOException {

        query = query.toLowerCase();
        query = query.replaceAll("\\.", " ");
        StringReader reader = new StringReader(query);
        Tokenizer tokenizer = new StandardTokenizer();
        tokenizer.setReader(reader);

        TokenStream tokenStream = new StopFilter(new ASCIIFoldingFilter(new ClassicFilter(new LowerCaseFilter(tokenizer))), EnglishAnalyzer.getDefaultStopSet());

        //tokenStream = new PorterStemFilter(tokenStream);
        final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();

        List<String> tokens = new ArrayList<>();
        while (tokenStream.incrementToken()) {
            tokens.add(charTermAttribute.toString());
        }

        tokenStream.end();
        tokenStream.close();

        return tokens;
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/hadoop/buildInvertedIndex")
    public void buildInvertedIndex() throws ParseException, IOException {
        System.out.println("Initiating inverted index building process...");
        ParseReducerOutput parseReducerOutput = new ParseReducerOutput(hadoopIndexPath, mongoTemplate);
        System.out.println("Inverted index build completed!");
    }

}
