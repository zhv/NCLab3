package framework.source;

import framework.StructuredData;

import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;

public interface Source extends Iterator<StructuredData>, Closeable {

}
