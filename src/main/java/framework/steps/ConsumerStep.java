package framework.steps;

import framework.StructuredData;

import java.util.Map;
import java.util.function.Consumer;

public class ConsumerStep extends Step {

    private Consumer<StructuredData> consumer;

    public ConsumerStep(Step prev, int threadCount, Consumer<StructuredData> consumer) {
        super(prev, threadCount);
        this.consumer = consumer;
    }

    @Override
    protected void action(StructuredData next, boolean isDone) {
        consumer.accept(next);

        queue.offer(next);
    }
}
