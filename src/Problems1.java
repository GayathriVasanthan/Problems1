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
    }
}
