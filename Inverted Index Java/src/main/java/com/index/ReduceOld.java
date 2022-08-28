package com.index;
// Importing libraries

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class ReduceOld extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

    // Reduce function
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter rep) throws IOException {


        HashMap<String, String[]> map = new HashMap<String, String[]>();

        for (Iterator<Text> it = values; it.hasNext(); ) {
            Text val = it.next();
            String[] data = val.toString().split(":");
            String articleID = data[0];
            String articleLength = data[1];
            if (map.containsKey(articleID)) {
                int count = Integer.parseInt(map.get(articleID)[0]) + 1;
                map.get(articleID)[0] = Integer.toString(count);

            } else {
                map.put(articleID, new String[]{"1", articleLength});
            }
        }

        StringBuilder articleValueList = new StringBuilder();
        for (String articleId : map.keySet()) {
            String[] data = map.get(articleId);
            articleValueList.append(articleId + ":" + data[0] + ":" + data[1] + " ");
        }

        output.collect(key, new Text(articleValueList.toString()));
    }
}