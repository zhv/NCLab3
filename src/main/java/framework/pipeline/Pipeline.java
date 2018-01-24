package framework.pipeline;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public class Pipeline {

    protected List<StepWithThreads> steps = new LinkedList<>();

    public void start(ExecutorService executorService) {
        for (StepWithThreads step : steps) {
            for (int i = 0; i < step.getThreadCount(); i++) {
                executorService.submit(step.getStep());
            }
        }
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
