package framework.source;

import framework.StructuredData;

import java.io.Closeable;
import java.util.Map;

public interface Result extends Closeable {

    // todo: move isDone to StructuredData
    void accept(StructuredData data, boolean isDone);
}
