package framework.source;

import framework.StructuredData;

import java.io.Closeable;
import java.util.Iterator;

public interface Source extends Iterator<StructuredData>, Closeable {

}
