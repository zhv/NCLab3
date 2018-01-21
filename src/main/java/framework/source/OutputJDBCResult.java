package framework.source;

import framework.StructuredData;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class OutputJDBCResult implements Result {

    private DataSource dataSource;
    private String query;
    private PreparedStatementBuilder preparedStatementBuilder;
    private PreparedStatement ps;

    public OutputJDBCResult(DataSource dataSource, String query, PreparedStatementBuilder preparedStatementBuilder) {
        this.dataSource = dataSource;
        this.query = query;
        this.preparedStatementBuilder = preparedStatementBuilder;
    }

    @Override
    public void accept(StructuredData data, boolean isDone) {
        init();
        try {
            preparedStatementBuilder.prepare(ps, data);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private synchronized void init() {
        if (ps == null) {
            try {
                Connection connection = dataSource.getConnection();
                ps = connection.prepareStatement(query);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
