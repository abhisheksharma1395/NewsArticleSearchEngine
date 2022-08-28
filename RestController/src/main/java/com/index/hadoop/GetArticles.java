package com.index.hadoop;

import java.util.*;

public class GetArticles {

    private final int TOTAL_DOCUMENTS = 138200;
    private List<WordIndex> wordIndices;

    public GetArticles(List<WordIndex> wordIndices) {
        this.wordIndices = wordIndices;
    }

    public List<Integer> getArticlesId(int skip) {

        List<Integer> articleIds = new ArrayList<>();
        Map<Integer, Float> map = new HashMap<>();

        for (WordIndex wordIndex : wordIndices) {
            int numberOfDocuments = wordIndex.getIndex().size();

            for (IndexData index : wordIndex.getIndex()) {
                float score = CalculateTF(index.termFrequency, index.documentSize) * CalculateIDF(numberOfDocuments);
                if (!map.containsKey(index.documentId)) map.put(index.documentId, score);
                else {
                    float newScore = map.get(index.documentId) + score;
                    map.put(index.documentId, newScore);
                }

            }
        }

        articleIds = new ArrayList<>(sortByValue(map).keySet());

        return getArticlesIdsAfterSkip(skip, articleIds);

    }

    private float CalculateTF(int wordCount, int docLength) {
        return wordCount * 1f / docLength;
    }

    private float CalculateIDF(int docsCount) {
        return (float) Math.log(TOTAL_DOCUMENTS * 1f / docsCount);
    }

    public Map<Integer, Float> sortByValue(Map<Integer, Float> map) {

        List<Map.Entry<Integer, Float>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        Map<Integer, Float> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Float> item : list)
            sortedMap.put(item.getKey(), item.getValue());

        return sortedMap;
    }

    private List<Integer> getArticlesIdsAfterSkip(int skip, List<Integer> articleIds) {

        List<Integer> result = new ArrayList<>();

        for (int i = skip; i < skip + 10; i++) {
            result.add(articleIds.get(i));
        }

        return result;
    }

}
