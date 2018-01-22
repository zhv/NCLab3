package framework.steps;

import framework.StructuredData;

import java.util.function.UnaryOperator;

public class FunctionStep extends Step {

    private UnaryOperator<StructuredData> function;

    public FunctionStep(Step prev, int threadCount, UnaryOperator<StructuredData> function) {
        this.function = function;
    }
}
