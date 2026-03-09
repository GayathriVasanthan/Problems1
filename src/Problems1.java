import java.util.*;

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
        }
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
    }
}
