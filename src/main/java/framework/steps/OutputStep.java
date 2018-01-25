package framework.steps;

import framework.StructuredData;

import java.util.concurrent.atomic.AtomicInteger;

public class OutputStep extends Step {

    private AtomicInteger inputCount;

    public OutputStep(int inputCount) {
        this.inputCount = new AtomicInteger(inputCount);
    }

    @Override
    protected StructuredData action(StructuredData data) {

        if (data.isLast() && inputCount.decrementAndGet() != 0)
            data.isLast(false);

        return data;
    }
}