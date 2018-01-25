package framework.source;

import framework.StructuredData;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementBuilder {

    void prepare(PreparedStatement ps, StructuredData data) throws SQLException;
}
