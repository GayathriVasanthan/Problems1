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
    }
}
