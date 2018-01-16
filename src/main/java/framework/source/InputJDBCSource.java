package framework.source;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author VYZH
 * @since 11.01.2018
 */
public class InputJDBCSource implements JDBCSource {

    private DataSource dataSource;
    private String query;
    private PreparedStatementBuilder preparedStatementBuilder;
    private volatile ResultSet resultSet;
    private volatile Boolean next;

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
    public synchronized ResultSet next() {
        init();
        next = null;
        return resultSet;
    }

    private synchronized void init() {
        if (resultSet == null) {
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(query);
                preparedStatementBuilder.prepare(ps, data);
                ps.execute();
                resultSet = ps.getResultSet();
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
