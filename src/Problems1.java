import java.util.*;
import java.util.concurrent.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;
    DNSEntry(String domain, String ipAddress, long ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
    }
    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class Problems1 {
    private static final int MAX_CACHE_SIZE = 1000;
    private static final Map<String, DNSEntry> cache = new LinkedHashMap<>(16, 0.75f, true);
    private static int hits = 0;
    private static int misses = 0;

    static {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            synchronized (cache) {
                Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();
                while (it.hasNext()) {
                    if (it.next().getValue().isExpired()) it.remove();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public static String resolve(String domain) {
        synchronized (cache) {
            DNSEntry entry = cache.get(domain);
            if (entry != null && !entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ipAddress;
            } else {
                misses++;
                String ip = queryUpstream(domain);
                cache.put(domain, new DNSEntry(domain, ip, 5));
                if (cache.size() > MAX_CACHE_SIZE) {
                    Iterator<String> it = cache.keySet().iterator();
                    if (it.hasNext()) cache.remove(it.next());
                }
                return "Cache MISS → " + ip;
            }
        }
    }

    private static String queryUpstream(String domain) {
        return "172.217." + new Random().nextInt(255) + "." + new Random().nextInt(255);
    }

    public static String getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
        return "Hit Rate: " + hitRate + "%, Hits: " + hits + ", Misses: " + misses;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(resolve("google.com"));
        System.out.println(resolve("google.com"));
        Thread.sleep(6000);
        System.out.println(resolve("google.com"));
        System.out.println(getCacheStats());

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
import java.util.concurrent.*;
import java.util.*;

class TokenBucket {
    int maxTokens;
    int tokens;
    long lastRefillTime;
    int refillRate;

    TokenBucket(int maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.tokens = maxTokens;
        this.refillRate = refillRate;
        this.lastRefillTime = System.currentTimeMillis();
    }

    synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        if (elapsed > 3600000) {
            tokens = maxTokens;
            lastRefillTime = now;
        }
    }

    int getRemaining() {
        refill();
        return tokens;
    }
}

public class Problems1 {
    private static final Map<String, TokenBucket> clients = new ConcurrentHashMap<>();
    private static final int LIMIT = 1000;

    public static String checkRateLimit(String clientId) {
        clients.computeIfAbsent(clientId, k -> new TokenBucket(LIMIT, LIMIT));
        TokenBucket bucket = clients.get(clientId);
        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemaining() + " requests remaining)";
        } else {
            long retryAfter = 3600 - ((System.currentTimeMillis() - bucket.lastRefillTime) / 1000);
            return "Denied (0 requests remaining, retry after " + retryAfter + "s)";
        }
    }

    public static String getRateLimitStatus(String clientId) {
        TokenBucket bucket = clients.get(clientId);
        if (bucket == null) return "Client not found";
        long reset = (bucket.lastRefillTime + 3600000) / 1000;
        int used = LIMIT - bucket.getRemaining();
        return "{used: " + used + ", limit: " + LIMIT + ", reset: " + reset + "}";
    }

    public static void main(String[] args) {
        System.out.println(checkRateLimit("abc123"));
        System.out.println(checkRateLimit("abc123"));
        System.out.println(getRateLimitStatus("abc123"));
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

class VideoData {
    String videoId;
    String data;
    VideoData(String videoId, String data) {
        this.videoId = videoId;
        this.data = data;
    }
}

public class Problems1 {
    private static final int L1_CAPACITY = 10000;
    private static final int L2_CAPACITY = 100000;

    private static final LinkedHashMap<String, VideoData> L1 = new LinkedHashMap<>(16, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
            return size() > L1_CAPACITY;
        }
    };

    private static final LinkedHashMap<String, VideoData> L2 = new LinkedHashMap<>(16, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
            return size() > L2_CAPACITY;
        }
    };

    private static final Map<String, Integer> accessCount = new HashMap<>();
    private static int L1Hits = 0, L2Hits = 0, L3Hits = 0, totalRequests = 0;
    private static double L1Time = 0.5, L2Time = 5, L3Time = 150;

    public static VideoData getVideo(String videoId) {
        totalRequests++;
        if (L1.containsKey(videoId)) {
            L1Hits++;
            return L1.get(videoId);
        }
        if (L2.containsKey(videoId)) {
            L2Hits++;
            VideoData data = L2.get(videoId);
            promoteToL1(videoId, data);
            return data;
        }
        L3Hits++;
        VideoData data = queryDatabase(videoId);
        L2.put(videoId, data);
        accessCount.put(videoId, 1);
        return data;
    }

    private static void promoteToL1(String videoId, VideoData data) {
        int count = accessCount.getOrDefault(videoId, 0) + 1;
        accessCount.put(videoId, count);
        if (count > 3) {
            L1.put(videoId, data);
class ParkingSpot {
    String licensePlate;
    long entryTime;
    boolean occupied;
}

public class Problems1 {
    private static final int SIZE = 500;
    private static final ParkingSpot[] lot = new ParkingSpot[SIZE];
    private static int totalVehicles = 0;
    private static int totalProbes = 0;
    private static final Map<Integer, Integer> hourlyOccupancy = new HashMap<>();

    private static int hash(String plate) {
        return Math.abs(plate.hashCode()) % SIZE;
    }

    public static String parkVehicle(String plate) {
        int index = hash(plate);
        int probes = 0;
        while (lot[index] != null && lot[index].occupied) {
            index = (index + 1) % SIZE;
            probes++;
        }
        if (lot[index] == null) lot[index] = new ParkingSpot();
        lot[index].licensePlate = plate;
        lot[index].entryTime = System.currentTimeMillis();
        lot[index].occupied = true;
        totalVehicles++;
        totalProbes += probes;
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        hourlyOccupancy.put(hour, hourlyOccupancy.getOrDefault(hour, 0) + 1);
        return "Assigned spot #" + index + " (" + probes + " probes)";
    }

    public static String exitVehicle(String plate) {
        int index = hash(plate);
        while (lot[index] != null) {
            if (lot[index].occupied && lot[index].licensePlate.equals(plate)) {
                lot[index].occupied = false;
                long duration = System.currentTimeMillis() - lot[index].entryTime;
                double hours = duration / 3600000.0;
                double fee = hours * 5.0;
                totalVehicles--;
                return "Spot #" + index + " freed, Duration: " + String.format("%.2f", hours) + "h, Fee: $" + String.format("%.2f", fee);
            }
            index = (index + 1) % SIZE;
        }
        return "Vehicle not found";
    }

    public static String getStatistics() {
        double occupancy = (totalVehicles * 100.0) / SIZE;
        double avgProbes = totalVehicles == 0 ? 0 : (totalProbes * 1.0 / totalVehicles);
        int peakHour = hourlyOccupancy.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(-1);
        return "Occupancy: " + String.format("%.2f", occupancy) + "%, Avg Probes: " + String.format("%.2f", avgProbes) + ", Peak Hour: " + peakHour;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(parkVehicle("ABC-1234"));
        System.out.println(parkVehicle("ABC-1235"));
        System.out.println(parkVehicle("XYZ-9999"));
        Thread.sleep(2000);
        System.out.println(exitVehicle("ABC-1234"));
        System.out.println(getStatistics());
class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time;
    Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class Problems1 {
    private static final List<Transaction> transactions = new ArrayList<>();

    public static void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public static List<String> findTwoSum(int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (map.containsKey(target - t.amount)) {
                Transaction other = map.get(target - t.amount);
                result.add("(" + other.id + "," + t.id + ")");
            }
            map.put(t.amount, t);
        }
        return result;
    }

    public static List<String> findTwoSumWithinHour(int target) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            for (int j = i + 1; j < transactions.size(); j++) {
                Transaction a = transactions.get(i);
                Transaction b = transactions.get(j);
                if (a.amount + b.amount == target && Math.abs(a.time - b.time) <= 3600000) {
                    result.add("(" + a.id + "," + b.id + ")");
                }
            }
        }
        return result;
    }

    public static List<List<Integer>> findKSum(int k, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), 0, k, target);
        return result;
    }

    private static void backtrack(List<List<Integer>> result, List<Integer> current, int start, int k, int target) {
        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }
        if (k == 0 || target < 0) return;
        for (int i = start; i < transactions.size(); i++) {
            current.add(transactions.get(i).id);
            backtrack(result, current, i + 1, k - 1, target - transactions.get(i).amount);
            current.remove(current.size() - 1);
        }
    }

    public static List<String> detectDuplicates() {
        Map<String, Map<Integer, Set<String>>> map = new HashMap<>();
        List<String> result = new ArrayList<>();
        for (Transaction t : transactions) {
            map.computeIfAbsent(t.merchant, m -> new HashMap<>());
            Map<Integer, Set<String>> amtMap = map.get(t.merchant);
            amtMap.computeIfAbsent(t.amount, a -> new HashSet<>()).add(t.account);
        }
        for (Map.Entry<String, Map<Integer, Set<String>>> e : map.entrySet()) {
            for (Map.Entry<Integer, Set<String>> a : e.getValue().entrySet()) {
                if (a.getValue().size() > 1) {
                    result.add("{amount:" + a.getKey() + ", merchant:" + e.getKey() + ", accounts:" + a.getValue() + "}");
                }
            }
        }
        return result;
    }

    private static VideoData queryDatabase(String videoId) {
        return new VideoData(videoId, "VideoContent_" + videoId);
    }

    public static String getStatistics() {
        double L1Rate = totalRequests == 0 ? 0 : (L1Hits * 100.0 / totalRequests);
        double L2Rate = totalRequests == 0 ? 0 : (L2Hits * 100.0 / totalRequests);
        double L3Rate = totalRequests == 0 ? 0 : (L3Hits * 100.0 / totalRequests);
        double avgTime = (L1Hits * L1Time + L2Hits * L2Time + L3Hits * L3Time) / totalRequests;
        return "L1: Hit Rate " + String.format("%.2f", L1Rate) + "%, Avg Time: " + L1Time + "ms\n" +
                "L2: Hit Rate " + String.format("%.2f", L2Rate) + "%, Avg Time: " + L2Time + "ms\n" +
                "L3: Hit Rate " + String.format("%.2f", L3Rate) + "%, Avg Time: " + L3Time + "ms\n" +
                "Overall: Hit Rate " + String.format("%.2f", (L1Rate + L2Rate + L3Rate)) + "%, Avg Time: " + String.format("%.2f", avgTime) + "ms";
    }

    public static void main(String[] args) {
        System.out.println(getVideo("video_123").data);
        System.out.println(getVideo("video_123").data);
        System.out.println(getVideo("video_999").data);
        System.out.println(getStatistics());
        addTransaction(new Transaction(1, 500, "Store A", "acc1", System.currentTimeMillis()));
        addTransaction(new Transaction(2, 300, "Store B", "acc2", System.currentTimeMillis() + 900000));
        addTransaction(new Transaction(3, 200, "Store C", "acc3", System.currentTimeMillis() + 1800000));
        addTransaction(new Transaction(4, 500, "Store A", "acc2", System.currentTimeMillis()));

        System.out.println(findTwoSum(500));
        System.out.println(findTwoSumWithinHour(500));
        System.out.println(findKSum(3, 1000));
        System.out.println(detectDuplicates());
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
