package framework;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class StructuredData {
    private Map<String, Object> data;

    public StructuredData(Map<String, Object> data) {
        this.data = new LinkedHashMap<>(data);
    }
    public StructuredData() {
        this.data = new LinkedHashMap<>();
    }

    public Map<String, Object> getMap() {
        return new LinkedHashMap<>(data);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public Double getAsDouble(String key) {
        return Double.parseDouble(data.get(key).toString());
    }

    public Integer getAsInt(String key) {
        return Integer.parseInt(data.get(key).toString());
    }

    public LocalDate getAsDate(String key, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        return LocalDate.parse(data.get(key).toString(), formatter);
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public int size() {
        return data.size();
    }

    public Set<Map.Entry<String,Object>> entrySet() {
        return data.entrySet();
    }

    @Override
    public String toString() {
        return "StructuredData{" +
                "data=" + data +
                '}';
    }
}
