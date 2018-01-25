package framework;

import java.util.LinkedHashMap;
import java.util.Map;

public class StructuredData {
    private Map<String, Object> data;
    private boolean isLast = false;

    public StructuredData(Map<String, Object> data) {
        this.data = new LinkedHashMap<>(data);
    }
    public StructuredData() {
        this.data = new LinkedHashMap<>();
    }

    public Map<String, Object> getMap() {
        return data;
    }

    public boolean isLast() {
        return isLast;
    }

    public void isLast(boolean isLast) {
        this.isLast = isLast;
    }

    @Override
    public String toString() {
        return "StructuredData{" +
                "data=" + data +
                '}';
    }
}
