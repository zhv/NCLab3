package framework.steps;

import framework.StructuredData;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Step implements Iterator<StructuredData> {

    final Queue<StructuredData> queue;
    final Step prev;
    Thread[] threads;
    boolean done = false;
    AtomicInteger threadCount;


    Step(Step prev, int threadCount) {
        this.prev = prev;
        this.threadCount = new AtomicInteger(threadCount);
        threads = new Thread[threadCount];
        queue = new ConcurrentLinkedQueue<>();
    }

    @SuppressWarnings("Duplicates")
    public void start() {
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                while (true) {
                    StructuredData next = null;
                    synchronized (prev) {
                        if (hasNext()) {
                            next = next();
                        }
                        else if (prev.done) {
                            break;
                        }
                        else {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ignore) { }
                        }
                    }
                    if (next != null) action(next, false);
                }

                if (threadCount.decrementAndGet() == 0) {
                    done = true;
                }
            });
            threads[i].setUncaughtExceptionHandler((t, e) -> {
                System.out.println(e);
                System.exit(1);
            });
            threads[i].start();
        }
    }

    protected void action(StructuredData next, boolean isDone) {}

    @Override
    public boolean hasNext() {
        return !prev.queue.isEmpty();
    }

    @Override
    public StructuredData next() {
        return prev.queue.poll();
    }
}
