import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.LinkedHashMap;
import java.util.Map;

public class Problems1{
    private static final ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();
    private static final Map<String, LinkedHashMap<Integer, Integer>> waitingList = new ConcurrentHashMap<>();

    public static void addProduct(String productId, int stock) {
        inventory.put(productId, new AtomicInteger(stock));
        waitingList.put(productId, new LinkedHashMap<>());
    }

    public static String checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        return stock == null ? "Product not found" : stock.get() + " units available";
    }

    public static String purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);
        if (stock == null) return "Product not found";
        while (true) {
            int currentStock = stock.get();
            if (currentStock > 0) {
                if (stock.compareAndSet(currentStock, currentStock - 1)) {
                    return "Success, " + (currentStock - 1) + " units remaining";
                }
            } else {
                LinkedHashMap<Integer, Integer> list = waitingList.get(productId);
                synchronized (list) {
                    int position = list.size() + 1;
                    list.put(userId, position);
                    return "Added to waiting list, position #" + position;
                }
            }
        }
    }

    public static void main(String[] args) {
        addProduct("IPHONE15_256GB", 100);
        System.out.println(checkStock("IPHONE15_256GB"));
        System.out.println(purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(purchaseItem("IPHONE15_256GB", 67890));
    }
}
