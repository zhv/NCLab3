package framework.steps;

import framework.StructuredData;
import framework.source.Result;

import java.util.Map;

public class OutputStep extends Step {

    private Result result;

    public OutputStep(Result result, Step prev, int threadCount) {
        super(prev, threadCount);
        this.result = result;
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
                    if (next != null) result.accept(next, false);
                }

                if (threadCount.decrementAndGet() == 0) {
                    result.accept(null, true);
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
}
