package framework.pipeline;

import framework.StructuredData;
import framework.source.Result;
import framework.source.Source;
import framework.steps.*;

import java.io.OutputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class PipelineBuilder {

    private Pipeline pipeline = new Pipeline();
    private boolean in = false;
    private boolean out = false;


    public void addInputStep(Source source, int threadCount) {
        in = true;
        pipeline.steps.add(new InputStep(source, threadCount));
    }

    public void addOutputStep(Result result, int threadCount) {
        if (pipeline.steps.size() != 0) {
            out = true;
            pipeline.steps.add(new OutputStep(result, pipeline.steps.get(pipeline.steps.size() - 1), threadCount));
        }
    }

    public void addMapStep(UnaryOperator<StructuredData> function, int threadCount) {
        if (pipeline.steps.size() != 0) {
            pipeline.steps.add(new FunctionStep(pipeline.steps.get(pipeline.steps.size() - 1), threadCount, function));
        }
    }

    public void addForEachStep(Consumer<StructuredData> consumer, int threadCount) {
        if (pipeline.steps.size() != 0) {
            pipeline.steps.add(new ConsumerStep(pipeline.steps.get(pipeline.steps.size() - 1), threadCount, consumer));
        }
    }

    public Pipeline getPipeline() {
        if (in && out) {

            while (!(pipeline.steps.get(0) instanceof InputStep)) {
                pipeline.steps.remove(0);
            }
            while (!(pipeline.steps.get(pipeline.steps.size() - 1) instanceof OutputStep)) {
                pipeline.steps.remove(pipeline.steps.size() - 1);
            }

            return pipeline;
        }

        throw new PipelineNotReadyException("Pipeline not ready");
    }
}
