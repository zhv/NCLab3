package framework.steps;

import framework.source.Result;
import framework.source.Source;

/**
 * @author VYZH
 * @since 11.01.2018
 */
public class Step implements Runnable {

    private Source source;
    private Result result;

    public Step(Source source, Result result) {
        this.source = source;
        this.result = result;
    }

    @Override
    public void run() {
        while (source.hasNext()) {
            Object data = source.next();
            result.accept(data);
        }
    }
}
