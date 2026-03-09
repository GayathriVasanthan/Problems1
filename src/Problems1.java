import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd;
    int frequency;
    String word;
}

public class Problems1 {
    private static final TrieNode root = new TrieNode();
    private static final Map<String, Integer> globalStats = new HashMap<>();

    public static void insert(String query, int freq) {
        globalStats.put(query, globalStats.getOrDefault(query, 0) + freq);
        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
        node.frequency = globalStats.get(query);
        node.word = query;
    }

    public static List<String> search(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return Collections.emptyList();
        }
        PriorityQueue<TrieNode> pq = new PriorityQueue<>((a, b) -> b.frequency - a.frequency);
        collect(node, pq);
        List<String> results = new ArrayList<>();
        int count = 0;
        while (!pq.isEmpty() && count < 10) {
            TrieNode n = pq.poll();
            results.add(n.word + " (" + n.frequency + " searches)");
            count++;
        }
        return results;
    }

    private static void collect(TrieNode node, PriorityQueue<TrieNode> pq) {
        if (node.isEnd) pq.add(node);
        for (TrieNode child : node.children.values()) {
            collect(child, pq);
        }
    }

    public static void main(String[] args) {
        insert("java tutorial", 1234567);
        insert("javascript", 987654);
        insert("java download", 456789);
        insert("javelin throw", 12345);
        insert("japan travel", 67890);

        List<String> suggestions = search("jav");
        for (int i = 0; i < suggestions.size(); i++) {
            System.out.println((i + 1) + ". " + suggestions.get(i));
        }
    }
}
