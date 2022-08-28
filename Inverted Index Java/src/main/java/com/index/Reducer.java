package com.index;
// Importing libraries

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class Reducer extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text, Text, Text, Text> {

    // Reduce function
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter rep) throws IOException {

        StringBuilder articleValueList = new StringBuilder();
        for (Iterator<Text> it = values; it.hasNext(); ) {
            Text val = it.next();
            articleValueList.append(val.toString() + " ");

        }

        output.collect(key, new Text(articleValueList.toString()));


    }
}