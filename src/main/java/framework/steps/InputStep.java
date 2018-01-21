package framework.steps;

import framework.StructuredData;
import framework.source.Source;

import java.util.Map;

public class InputStep extends Step {

    private final Source source;

    public InputStep(Source source, int threadCount) {
        super(null, threadCount);
        this.source = source;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void start() {
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                while (hasNext()) {
                    synchronized (source) {
                        if (hasNext()) {
                            queue.offer(next());
                        }
                    }
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

    @Override
    public boolean hasNext() {
        return source.hasNext();
    }

    @Override
    public StructuredData next() {
        return source.next();
    }
}
