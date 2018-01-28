package framework.steps;

import framework.FunctionExceptionAction;
import framework.StructuredData;

import java.util.function.Function;

public class FunctionStep extends Step {

    private Function<Object, ?> function;
    private String key;
    private FunctionExceptionAction functionExceptionAction;

    public FunctionStep(String key, Function<Object, ?> function, FunctionExceptionAction functionExceptionAction) {
        this.function = function;
        this.key = key;
        this.functionExceptionAction = functionExceptionAction;
    }

    @Override
    protected StructuredData action(StructuredData data) {

        try {
            Object value = data.getMap().get(key).toString();
            Object newValue = function.apply(value);
            data.getMap().put(key, newValue);
        } catch (Exception e) {
            if (functionExceptionAction == FunctionExceptionAction.THROW_EXCEPTION) {
                throw new IllegalStateException(e);
            } else if (functionExceptionAction == FunctionExceptionAction.STOP_PIPELINE) {
                status.setRunning(false);
                return null;
            }
        }

        return data;
    }
}
