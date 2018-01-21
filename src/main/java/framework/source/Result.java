package framework.source;

import framework.StructuredData;

import java.util.Map;

public interface Result {

    void accept(StructuredData data, boolean isDone);
}
