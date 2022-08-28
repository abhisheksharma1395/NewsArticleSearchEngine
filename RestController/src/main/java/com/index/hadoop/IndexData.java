package com.index.hadoop;

import java.io.Serializable;

public class IndexData implements Serializable {

    private static final long serialVersionUID = -5294188737237640025L;

    int documentId;
    int termFrequency;
    int documentSize;

    public IndexData(int documentId, int termFrequency, int documentSize) {
        this.documentId = documentId;
        this.termFrequency = termFrequency;
        this.documentSize = documentSize;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IndexData{");
        sb.append("documentId=").append(documentId);
        sb.append(", termFrequency=").append(termFrequency);
        sb.append(", documentSize=").append(documentSize);
        sb.append('}');
        return sb.toString();
    }
}
