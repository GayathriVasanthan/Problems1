import java.util.*;

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
    }
}
