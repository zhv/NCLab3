package framework.source;

import framework.StructuredData;

import java.io.Closeable;

public interface Result extends Closeable {

    void accept(StructuredData data);
}
