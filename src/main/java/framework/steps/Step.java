package framework.steps;

import framework.StructuredData;
import framework.pipeline.PipelineStatus;
import framework.source.Result;
import framework.source.Source;

public class Step implements Runnable {

    protected Source source;
    protected Result result;
    protected PipelineStatus status;


    public void setSource(Source source) {
        this.source = source;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void setStatus(PipelineStatus status) {
        this.status = status;
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

                data = action(data);

                if (!status.isRunning()) {
                    System.out.println(this + " stopped");
                    break;
                }

                result.accept(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected StructuredData action(StructuredData data) {
        return data;
    }

    @Override
    public String toString() {
        return "Step(" + source + " => " + result + ")";
    }
}
