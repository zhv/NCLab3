package framework.source;

import framework.StructuredData;

import java.sql.PreparedStatement;
import java.util.Map;

public interface PreparedStatementBuilder {

    void prepare(PreparedStatement ps, StructuredData data);
}
