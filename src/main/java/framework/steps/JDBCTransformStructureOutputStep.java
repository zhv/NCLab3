package framework.steps;

import java.sql.ResultSet;

public class JDBCTransformStructureOutputStep extends TransformStructureStep<StructuredData, ResultSet> {
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public ResultSet next() {
        return null;
    }
}
