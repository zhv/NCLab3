package framework.pipeline;

import framework.FunctionExceptionAction;
import framework.source.Result;
import framework.source.Source;
import framework.source.StepConnector;
import framework.steps.FunctionStep;
import framework.steps.Step;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class PipelineBuilder {

    private int inputStepCount = 0;
    private Pipeline pipeline = new Pipeline();

    private StepConnector connector;

    public void addInputStep(Source source, int threadCount) {
        if (connector == null) connector = createConnector();
        pipeline.steps.add(new StepWithThreads(createStep(source, connector, threadCount), threadCount));
        inputStepCount++;
    }

    public void addOutputStep(Result result, int threadCount) {
        if (connector == null) {
            throw new PipelineNotReadyException("Pipeline doesn't contain any previous step");
        }
        pipeline.steps.add(new StepWithThreads(createStep(connector, result, threadCount), threadCount));
    }

    public void addStep(Step step, int threadCount) {
        if (connector == null) {
            throw new PipelineNotReadyException("Pipeline doesn't contain any previous step");
        }
        StepConnector prevConnector = connector;
        connector = createConnector();
        pipeline.steps.add(new StepWithThreads(createStep(prevConnector, connector, step, threadCount), threadCount));
    }

    public void addFunction(String key, Function<Object, ?> function, FunctionExceptionAction functionExceptionAction, int threadCount) {
        if (connector == null) {
            throw new PipelineNotReadyException("Pipeline doesn't contain any previous step");
        }
        StepConnector prevConnector = connector;
        connector = createConnector();
        Step step = new FunctionStep(key, function, functionExceptionAction);
        pipeline.steps.add(new StepWithThreads(createStep(prevConnector, connector, step, threadCount), threadCount));
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    private Step createStep(Source source, Result result, int threadCount) {
        Step step = new Step();
        step.setSource(source);
        step.setResult(result);
        step.setStatus(pipeline.getStatus());
        step.setThreadCount(new AtomicInteger(threadCount));
        return step;
    }

    private Step createStep(Source source, Result result, Step step, int threadCount) {
        step.setSource(source);
        step.setResult(result);
        step.setStatus(pipeline.getStatus());
        step.setThreadCount(new AtomicInteger(threadCount));
        return step;
    }

    private StepConnector createConnector() {
        return new StepConnector(new LinkedBlockingQueue<>());
    }
}
