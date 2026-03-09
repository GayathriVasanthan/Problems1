import java.util.*;

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

    public static void main(String[] args) {
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
