package framework.steps;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCTransformStructureInputStep extends TransformStructureStep<ResultSet, Map<String, String>> {

    private ResultSet resultSet;

    public JDBCTransformStructureInputStep() {

    }

    @Override
    public boolean hasNext() {
        return prev.hasNext();
    }

    @Override
    public Map<String, String> next() {
        ResultSet rs;
        ResultSetMetaData resultSetMetaData;
        int count;
        List<String> cols;
        Map<String, String> row = new HashMap<>();

        try {

            rs = prev.next();
            resultSetMetaData = rs.getMetaData();
            count = resultSetMetaData.getColumnCount();
            cols = new ArrayList<>(count);

            for (int i = 1; i <= count; i++)
                cols.add(resultSetMetaData.getColumnName(i));

            for (String col : cols)
                row.put(col, rs.getString(col));


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return row;
    }
}
