package framework.source;

import framework.StructuredData;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author VYZH
 * @since 22.01.2018
 */
public class StepConnector implements Source, Result {

    private final BlockingQueue<StructuredData> queue;
    private volatile StructuredData last;

    public StepConnector(BlockingQueue<StructuredData> queue) {
        this.queue = queue;
    }

    @Override
    public void accept(StructuredData data, boolean isDone) {
        queue.add(data);
    }

    @Override
    public boolean hasNext() {
        getLast();
        return last != null;
    }

    @Override
    public StructuredData next() {
        getLast();
        return last;
    }

    @Override
    public void close() {
        // nothing to do
    }

    protected synchronized void getLast() {
        try {
            last = null;
            last = queue.poll(2, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) { }
    }
}
