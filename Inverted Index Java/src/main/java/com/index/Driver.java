package com.index;

import java.io.IOException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Tool;

public class Driver extends Configured implements Tool {

    public int run(String args[]) throws IOException {
//        if (args.length < 2) {
//            System.out.println("Please give valid inputs");
//            return -1;
//        }

        JobConf conf = new JobConf(Driver.class);
        //FileInputFormat.setInputPaths(conf, new Path(args[0]));
        //FileOutputFormat.setOutputPath(conf, new Path(args[1]));
        
        FileInputFormat.setInputPaths(conf, new Path("C:\\IRProject\\data\\"));
        FileOutputFormat.setOutputPath(conf, new Path("C:\\IRProject\\hadoopData\\"));
        conf.setMapperClass(Mapper.class);
        conf.setCombinerClass(Combiner.class);
        conf.setReducerClass(Reducer.class);
        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(Text.class);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        JobClient.runJob(conf);
        return 0;
    }
}
