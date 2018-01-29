package framework;

import java.util.LinkedHashMap;
import java.util.Map;

public class StructuredData {
    private Map<String, Object> data;

    public StructuredData(Map<String, Object> data) {
        this.data = new LinkedHashMap<>(data);
    }
    public StructuredData() {
        this.data = new LinkedHashMap<>();
    }

    public Map<String, Object> getMap() {
        return data;
    }

    @Override
    public String toString() {
        return "StructuredData{" +
                "data=" + data +
                '}';
    }
}
