package framework.source;

import java.sql.ResultSet;

public class OutputJDBCSourse implements JDBCSource {



    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public ResultSet next() {
        return null;
    }
}
