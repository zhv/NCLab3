package framework.source;

import framework.StructuredData;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class InputJDBCSource implements Source {

    private DataSource dataSource;
    private String query;
    private PreparedStatementBuilder preparedStatementBuilder;
    private volatile ResultSet resultSet;
    private volatile Boolean next;
    private ResultSetMetaData metaData;
    private int columnCount;

    public InputJDBCSource(DataSource dataSource, String query, PreparedStatementBuilder preparedStatementBuilder) {
        this.dataSource = dataSource;
        this.query = query;
        this.preparedStatementBuilder = preparedStatementBuilder;
    }

    @Override
    public synchronized boolean hasNext() {
        init();
        if (next == null) {
            try {
                next = resultSet.next();
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
        return next;
    }

    @Override
    public synchronized StructuredData next() {
        init();
        next = null;

        Map<String, Object> row = new LinkedHashMap<>();

        try {

            for (int i = 1; i <= columnCount; i++)
                row.put(metaData.getColumnName(i), resultSet.getObject(i));

        } catch (Exception ignore) { }


        return new StructuredData(row);
    }

    private synchronized void init() {
        if (resultSet == null) {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query);
                preparedStatementBuilder.prepare(ps, null);
                ps.execute();
                resultSet = ps.getResultSet();
                metaData = resultSet.getMetaData();
                columnCount = metaData.getColumnCount();
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
