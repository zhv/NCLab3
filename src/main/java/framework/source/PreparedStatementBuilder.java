package framework.source;

import java.sql.PreparedStatement;

/**
 * @author VYZH
 * @since 11.01.2018
 */
public interface PreparedStatementBuilder {

    void prepare(PreparedStatement ps);
}
