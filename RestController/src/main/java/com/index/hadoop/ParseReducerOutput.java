package com.index.hadoop;

import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ParseReducerOutput {
    private final MongoTemplate mongoTemplate;


    public ParseReducerOutput(String invertedIndexPath, MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        readInvertedTextFile(invertedIndexPath);
    }

    private void readInvertedTextFile(String invertedIndexPath) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(invertedIndexPath));
            String line = bufferedReader.readLine();
            while (line != null) {
                addData(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (Exception ex) {
            System.out.println("Error while reading the file!");
            System.out.println(ex.toString());
            ex.printStackTrace();
        }
    }

    private void addData(String line) {
        if (line != null) {
            line = line.trim();
            String[] docs = line.split("\\s+");
            if (docs[0].equals("apprise")) {
                System.out.println("Here");
            }
            if (docs[0].equals("06'08'10'12'14'16'18'2040")) {
                System.out.println("Here");
            }
            List<IndexData> list = new ArrayList<IndexData>();
            for (int i = 1; i < docs.length; i++) {
                String doc = docs[i];
                doc = doc.trim();
                String[] data = doc.split(":");
                IndexData index = new IndexData(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                list.add(index);
            }

            saveDataInDB(new WordIndex(docs[0], list));
        }
    }

    private void saveDataInDB(WordIndex data) {
        try {
            mongoTemplate.save(data);
            System.out.println("Successfully saved data for " + data.getWord() + " to DB");
        } catch (Exception ex) {
            System.out.println("Error while saving" + data.getWord() + " to DB!");
        }
    }
}
