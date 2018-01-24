package framework.source;

import framework.StructuredData;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author VYZH
 * @since 22.01.2018
 */
public class StepConnector implements Source, Result {

    private final BlockingQueue<StructuredData> queue;
    private final BlockingQueue<StructuredData> internalQueue = new LinkedBlockingQueue<>();

    public StepConnector(BlockingQueue<StructuredData> queue) {
        this.queue = queue;
    }

    @Override
    public void accept(StructuredData data, boolean isDone) {
        queue.add(data);
    }

    @Override
    public boolean hasNext() {
        fetch();
        return !internalQueue.isEmpty();
    }

    @Override
    public StructuredData next() {
        fetch();
        return internalQueue.poll();
    }

    @Override
    public void close() {
        // nothing to do
    }

    protected void fetch() {
        try {
            StructuredData data = queue.poll(2, TimeUnit.SECONDS);
            if (data != null) {
                internalQueue.put(data);
            }
        } catch (InterruptedException ignore) { }
    }
}
