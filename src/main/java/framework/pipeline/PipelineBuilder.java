package framework.pipeline;

import framework.source.Result;
import framework.source.Source;
import framework.source.StepConnector;
import framework.steps.Step;

import java.util.concurrent.LinkedBlockingQueue;

public class PipelineBuilder {

    private Pipeline pipeline = new Pipeline();
    private boolean in = false;
    private boolean out = false;

    private StepConnector connector;

    public void addInputStep(Source source, int threadCount) {
        connector = createConnector();
        pipeline.steps.add(new StepWithThreads(createStep(source, connector), threadCount));
    }

    public void addOutputStep(Result result, int threadCount) {
        if (connector == null) {
            throw new IllegalStateException("Pipeline doesn't contain any previous step");
        }
        pipeline.steps.add(new StepWithThreads(createStep(connector, result), threadCount));
        connector = createConnector();
    }

    public void addStep(Step step, int threadCount) {
        if (connector == null) {
            throw new IllegalStateException("Pipeline doesn't contain any previous step");
        }
        StepConnector prevConnector = connector;
        connector = createConnector();
        pipeline.steps.add(new StepWithThreads(createStep(prevConnector, connector), threadCount));
    }

//    public void addMapStep(UnaryOperator<StructuredData> function, int threadCount) {
//    }
//        }
//            pipeline.steps.add(new ConsumerStep(pipeline.steps.get(pipeline.steps.size() - 1), threadCount, consumer));
//        if (pipeline.steps.size() != 0) {
//        }
//            throw new IllegalStateException("Pipeline doesn't contain any previous step");
//        if (connector == null) {
//    public void addForEachStep(Consumer<StructuredData> consumer, int threadCount) {
//
//    }
//        }
//            pipeline.steps.add(new FunctionStep(pipeline.steps.get(pipeline.steps.size() - 1), threadCount, function));
//        if (pipeline.steps.size() != 0) {
//        }
//            throw new IllegalStateException("Pipeline doesn't contain any previous step");
//        if (connector == null) {

    public Pipeline getPipeline() {
        return pipeline;
    }

    protected Step createStep(Source source, Result result) {
        Step step = new Step();
        step.setSource(source);
        step.setResult(result);
        return step;
    }

    protected StepConnector createConnector() {
        return new StepConnector(new LinkedBlockingQueue<>());
    }
}
