import java.util.*;

public class Problems1 {
    private static final Map<String, Set<String>> ngramIndex = new HashMap<>();
    private static final int N = 5;

    public static void indexDocument(String docId, String text) {
        List<String> words = Arrays.asList(text.split("\\s+"));
        for (int i = 0; i <= words.size() - N; i++) {
            String ngram = String.join(" ", words.subList(i, i + N));
            ngramIndex.computeIfAbsent(ngram, k -> new HashSet<>()).add(docId);
        }
    }

    public static Map<String, Double> analyzeDocument(String docId, String text) {
        List<String> words = Arrays.asList(text.split("\\s+"));
        Map<String, Integer> matchCounts = new HashMap<>();
        int totalNgrams = 0;
        for (int i = 0; i <= words.size() - N; i++) {
            totalNgrams++;
            String ngram = String.join(" ", words.subList(i, i + N));
            Set<String> docs = ngramIndex.get(ngram);
            if (docs != null) {
                for (String d : docs) {
                    if (!d.equals(docId)) {
                        matchCounts.put(d, matchCounts.getOrDefault(d, 0) + 1);
                    }
                }
            }
        }
        Map<String, Double> similarity = new HashMap<>();
        for (Map.Entry<String, Integer> e : matchCounts.entrySet()) {
            similarity.put(e.getKey(), (e.getValue() * 100.0) / totalNgrams);
        }
        return similarity;
    }

    public static void main(String[] args) {
        String doc1 = "this is a sample essay with some unique content";
        String doc2 = "this is a sample essay with some plagiarized content";
        String doc3 = "completely different text with no overlap";

        indexDocument("essay_089.txt", doc1);
        indexDocument("essay_092.txt", doc2);
        indexDocument("essay_100.txt", doc3);

        Map<String, Double> result = analyzeDocument("essay_123.txt", "this is a sample essay with some plagiarized content");
        for (Map.Entry<String, Double> e : result.entrySet()) {
            System.out.println("Found similarity with " + e.getKey() + ": " + e.getValue() + "%");
        }
    }
}
