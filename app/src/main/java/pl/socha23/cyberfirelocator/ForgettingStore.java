package pl.socha23.cyberfirelocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForgettingStore<T> {

    private Map<String, T> items = new HashMap<>();
    private Map<String, Long> timestamps = new HashMap<>();

    private int memoryTimeMillis = 1000;

    public ForgettingStore(int memoryTimeMillis) {
        this.memoryTimeMillis = memoryTimeMillis;
    }

    public List<T> list() {
        List<String> keys = new ArrayList<>(items.keySet());
        Collections.sort(keys);
        List<T> result = new ArrayList<>();
        for (String key : keys) {
            if (System.currentTimeMillis() - timestamps.get(key) <= memoryTimeMillis) {
                result.add(items.get(key));
            }
        }
        return result;
    }

    public void put(String id, T item, long timestamp) {
        items.put(id, item);
        timestamps.put(id, timestamp);
    }


    public void put(String id, T item) {
        put(id, item, System.currentTimeMillis());
    }
}
