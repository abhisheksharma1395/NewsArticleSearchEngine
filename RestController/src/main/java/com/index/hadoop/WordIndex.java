package com.index.hadoop;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Document("WordIndex")
public class WordIndex implements Serializable {

    private static final long serialVersionUID = -5294188737237640015L;

    String word;
    List<IndexData> index;

    public WordIndex(String word, List<IndexData> index) {
        this.word = word;
        this.index = index;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<IndexData> getIndex() {
        return index;
    }

    public void setIndex(List<IndexData> index) {
        this.index = index;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WordIndex{");
        sb.append("word='").append(word).append('\'');
        sb.append(", index=").append(index);
        sb.append('}');
        return sb.toString();
    }
}
