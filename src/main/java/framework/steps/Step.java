package framework.steps;

import framework.StructuredData;
import framework.Tuple;
import framework.pipeline.Pipeline;
import framework.source.Result;
import framework.source.Source;

public class Step implements Runnable {

    protected Source source;
    protected Result result;
    private boolean isRunning = true;
    private Pipeline pipeline;


    public void setSource(Source source) {
        this.source = source;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                StructuredData data;
                synchronized (source) {
                    if (!source.hasNext()) {
                        break;
                    }
                    data = source.next();
                    System.out.println("got data = " + data + " : " + this);
                }

                Tuple<StructuredData, Boolean> actionRes = action(data);

                if (actionRes.getItem2() && pipeline.isRunning()) {
                    System.out.println(pipeline + " stopped");
                    pipeline.stop();
                }

                if (!isRunning) {
                    System.out.println(this + " stopped");
                    break;
                }

                data = actionRes.getItem1();
                result.accept(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Tuple<StructuredData, Boolean> action(StructuredData data) {
        return new Tuple<>(data, false);
    }

    @Override
    public String toString() {
        return "Step(" + source + " => " + result + ")";
    }
}
