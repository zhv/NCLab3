package framework.pipeline;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Pipeline {

    protected List<StepWithThreads> steps = new LinkedList<>();
    private boolean isRunning = false;

    public void start(ExecutorService executorService) {
        if (!isRunning) {
            for (StepWithThreads step : steps) {
                for (int i = 0; i < step.getThreadCount(); i++) {
                    executorService.submit(step.getStep());
                }
            }

            isRunning = true;
        }
    }

    public void stop() {
        if (isRunning) {
            for (StepWithThreads step : steps) {
                step.getStep().setRunning(false);
            }

            isRunning = false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Pipeline(\n");
        for (StepWithThreads step : steps) {
            sb.append(step).append("\n");
        }
        return sb.append(")").toString();
    }
}
