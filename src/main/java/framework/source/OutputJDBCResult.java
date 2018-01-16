package framework.source;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OutputJDBCResult<E> implements Result<E> {

    private DataSource dataSource;
    private String query;
    private PreparedStatementBuilder<E> preparedStatementBuilder;
    private PreparedStatement ps;

    public OutputJDBCResult(DataSource dataSource, String query, PreparedStatementBuilder<E> preparedStatementBuilder) {
        this.dataSource = dataSource;
        this.query = query;
        this.preparedStatementBuilder = preparedStatementBuilder;
    }

    @Override
    public void accept(E data) {
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
