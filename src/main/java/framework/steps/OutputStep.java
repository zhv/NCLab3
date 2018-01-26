package framework.steps;

import framework.StructuredData;
import framework.Tuple;

import java.util.concurrent.atomic.AtomicInteger;

public class OutputStep extends Step {

    private AtomicInteger inputCount;

    public OutputStep(int inputCount) {
        this.inputCount = new AtomicInteger(inputCount);
    }

    @Override
    protected Tuple<StructuredData, Boolean> action(StructuredData data) {

        if (data.isLast() && inputCount.decrementAndGet() != 0)
            data.isLast(false);

        return new Tuple<>(data, false);
    }
}
