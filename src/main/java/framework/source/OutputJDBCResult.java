package framework.source;

import framework.StructuredData;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OutputJDBCResult implements Result {

    private DataSource dataSource;
    private String query;
    private PreparedStatementBuilder preparedStatementBuilder;
    private PreparedStatement ps;
    private Connection connection;

    public OutputJDBCResult(DataSource dataSource, String query, PreparedStatementBuilder preparedStatementBuilder) {
        this.dataSource = dataSource;
        this.query = query;
        this.preparedStatementBuilder = preparedStatementBuilder;
    }

    @Override
    public synchronized void accept(StructuredData data) {
        init();
        try {
            preparedStatementBuilder.prepare(ps, data);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private synchronized void init() {
        if (ps == null) {
            try {
                connection = dataSource.getConnection();
                ps = connection.prepareStatement(query);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public void close() {
        if (connection != null || ps != null) {
            try {
                ps.close();
                connection.close();
                ps = null;
                connection = null;
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
