package com.index;

// Importing libraries

import java.io.IOException;


import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.StringReader;
import java.util.regex.Pattern;


import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


public class Mapper extends MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text, Text, Text> {

    // Map function
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter rep) throws IOException {

        try {

            JSONArray articles = (JSONArray) new JSONParser().parse(value.toString());

            for (int i = 0; i < articles.size(); i++) {

                JSONObject article = (JSONObject) articles.get(i);

                String articleID = article.getAsString("articleID");
                String articleBody = article.getAsString("articleBody");
                articleBody = articleBody.toLowerCase();
                articleBody = articleBody.replaceFirst(Pattern.quote("AdvertisementSupported by"), "");
                articleBody = articleBody.replaceAll("Advertisement$", "");
                articleBody = articleBody.replaceAll("\\.", " ");
                int length = articleBody.split("\\s+").length;
                StringReader reader = new StringReader(articleBody);
                Tokenizer tokenizer = new StandardTokenizer();
                tokenizer.setReader(reader);


                TokenStream tokenStream = new StopFilter(new ASCIIFoldingFilter(new ClassicFilter(new LowerCaseFilter(tokenizer))), EnglishAnalyzer.getDefaultStopSet());

                //tokenStream = new PorterStemFilter(tokenStream);
                final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
                tokenStream.reset();

                while (tokenStream.incrementToken()) {

                    output.collect(new Text(charTermAttribute.toString()), new Text(articleID + ":" + Integer.toString(length)));
                }

                tokenStream.end();
                tokenStream.close();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}