package com.index;


import org.apache.commons.io.FileUtils;
import org.apache.hadoop.util.ToolRunner;

import java.io.File;

/**
 * Hello world!
 */
public class App {
    public static void main(String args[]) throws Exception {

        //File dir = new File(args[1]);
    	File dir = new File("C:\\IRProject\\hadoopData\\");
        FileUtils.deleteDirectory(dir);

        int exitCode = ToolRunner.run(new Driver(), args);


    }
}
