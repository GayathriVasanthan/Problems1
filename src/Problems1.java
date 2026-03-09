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
