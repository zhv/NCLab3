package framework.steps;

import framework.StructuredData;
import framework.source.Result;
import framework.source.Source;

public class Step implements Runnable {

    protected Source source;
    protected Result result;

    public void setSource(Source source) {
        this.source = source;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted() && source.hasNext()) {
                StructuredData data = source.next();
                result.accept(data, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Step(" + source + " => " + result + ")";
    }
}
