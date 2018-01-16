package framework.steps;

import java.util.Iterator;
import java.util.Map;

public class StructuredData implements Iterator {

    private Map<String, String> data;

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Object next() {
        return null;
    }
}
