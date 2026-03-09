import java.util.*;
import java.util.concurrent.*;

class PageStats {
    int views;
    Set<String> uniqueUsers = new HashSet<>();
    PageStats() { views = 0; }
}

public class Problems1 {
    private static final Map<String, PageStats> pageViews = new ConcurrentHashMap<>();
    private static final Map<String, Integer> trafficSources = new ConcurrentHashMap<>();

    public static void processEvent(String url, String userId, String source) {
        pageViews.computeIfAbsent(url, k -> new PageStats());
        PageStats stats = pageViews.get(url);
        synchronized (stats) {
            stats.views++;
            stats.uniqueUsers.add(userId);
        }
        trafficSources.merge(source, 1, Integer::sum);
    }

    public static String getDashboard() {
        List<Map.Entry<String, PageStats>> sortedPages = new ArrayList<>(pageViews.entrySet());
        sortedPages.sort((a, b) -> b.getValue().views - a.getValue().views);
        StringBuilder sb = new StringBuilder();
        sb.append("Top Pages:\n");
        for (int i = 0; i < Math.min(10, sortedPages.size()); i++) {
            Map.Entry<String, PageStats> e = sortedPages.get(i);
            sb.append((i + 1) + ". " + e.getKey() + " - " + e.getValue().views + " views (" + e.getValue().uniqueUsers.size() + " unique)\n");
        }
        sb.append("Traffic Sources:\n");
        for (Map.Entry<String, Integer> e : trafficSources.entrySet()) {
            sb.append(e.getKey() + ": " + e.getValue() + "\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println(getDashboard());
        }, 0, 5, TimeUnit.SECONDS);

        processEvent("/article/breaking-news", "user_123", "google");
        processEvent("/article/breaking-news", "user_456", "facebook");
        processEvent("/sports/championship", "user_789", "direct");
        processEvent("/sports/championship", "user_123", "google");

        Thread.sleep(15000);
        scheduler.shutdown();
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

public class Problems1 {
    private static final ConcurrentHashMap<String, Integer> users = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AtomicInteger> attempts = new ConcurrentHashMap<>();

    public static void registerUser(String username, int userId) {
        users.put(username, userId);
    }

    public static boolean checkAvailability(String username) {
        attempts.computeIfAbsent(username, k -> new AtomicInteger(0)).incrementAndGet();
        return !users.containsKey(username);
    }

    public static List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            String alt = username + i;
            if (!users.containsKey(alt)) suggestions.add(alt);
        }
        String alt1 = username.replace("_", ".");
        if (!users.containsKey(alt1)) suggestions.add(alt1);
        return suggestions;
    }

    public static String getMostAttempted() {
        return attempts.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().get()))
                .map(Map.Entry::getKey)
                .orElse("None");
    }

    public static void main(String[] args) {
        registerUser("john_doe", 1);
        registerUser("admin", 2);
        System.out.println(checkAvailability("john_doe"));
        System.out.println(checkAvailability("jane_smith"));
        System.out.println(suggestAlternatives("john_doe"));
        System.out.println(getMostAttempted());
    }
}
