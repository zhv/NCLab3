package framework.steps;

import framework.StructuredData;

import java.util.Map;
import java.util.function.UnaryOperator;

public class FunctionStep extends Step {

    private UnaryOperator<StructuredData> function;

    public FunctionStep(Step prev, int threadCount, UnaryOperator<StructuredData> function) {
        super(prev, threadCount);
        this.function = function;
    }

    @Override
    protected void action(StructuredData next, boolean isDone) {
        next = function.apply(next);

        queue.offer(next);
    }
}
