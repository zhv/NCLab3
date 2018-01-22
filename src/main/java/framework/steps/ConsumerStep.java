package framework.steps;

import framework.StructuredData;

import java.util.function.Consumer;

public class ConsumerStep extends Step {

    private Consumer<StructuredData> consumer;

    public ConsumerStep(Step prev, int threadCount, Consumer<StructuredData> consumer) {
        this.consumer = consumer;
    }
}
