package framework.pipeline;

import framework.FunctionExceptionAction;
import framework.source.Result;
import framework.source.Source;
import framework.source.StepConnector;
import framework.steps.FunctionStep;
import framework.steps.OutputStep;
import framework.steps.Step;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

public class PipelineBuilder {

    private int inputStepCount = 0;
    private Pipeline pipeline = new Pipeline();

    private StepConnector connector;

    public void addInputStep(Source source, int threadCount) {
        if (connector == null) connector = createConnector();
        pipeline.steps.add(new StepWithThreads(createStep(source, connector), threadCount));
        inputStepCount++;
    }

    public void addOutputStep(Result result, int threadCount) {
        if (connector == null) {
            throw new PipelineNotReadyException("Pipeline doesn't contain any previous step");
        }
        pipeline.steps.add(new StepWithThreads(createOutputStep(connector, result, inputStepCount), threadCount));
    }

    public void addStep(Step step, int threadCount) {
        if (connector == null) {
            throw new PipelineNotReadyException("Pipeline doesn't contain any previous step");
        }
        StepConnector prevConnector = connector;
        connector = createConnector();
        pipeline.steps.add(new StepWithThreads(createStep(prevConnector, connector, step), threadCount));
    }

    public void addFunction(String key, Function<Object, ?> function, FunctionExceptionAction functionExceptionAction, int threadCount) {
        if (connector == null) {
            throw new PipelineNotReadyException("Pipeline doesn't contain any previous step");
        }
        StepConnector prevConnector = connector;
        connector = createConnector();
        Step step = new FunctionStep(key, function, functionExceptionAction);
        pipeline.steps.add(new StepWithThreads(createStep(prevConnector, connector, step), threadCount));
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    private Step createStep(Source source, Result result) {
        Step step = new Step();
        step.setSource(source);
        step.setResult(result);
        step.setPipeline(pipeline);
        return step;
    }

    private Step createStep(Source source, Result result, Step step) {
        step.setSource(source);
        step.setResult(result);
        step.setPipeline(pipeline);
        return step;
    }

    private Step createOutputStep(Source source, Result result, int inputStepCount) {
        Step step = new OutputStep(inputStepCount);
        step.setSource(source);
        step.setResult(result);
        step.setPipeline(pipeline);
        return step;
    }

    private StepConnector createConnector() {
        return new StepConnector(new LinkedBlockingQueue<>());
    }
}
