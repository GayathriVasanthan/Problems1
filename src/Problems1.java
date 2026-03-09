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
    }
}
