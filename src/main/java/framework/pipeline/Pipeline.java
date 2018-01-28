package framework.pipeline;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Pipeline {

    protected List<StepWithThreads> steps = new LinkedList<>();
    private PipelineStatus status = new PipelineStatus();

    public void start(ExecutorService executorService) {
        if (!status.isRunning()) {
            status.setRunning(true);

            for (StepWithThreads step : steps) {
                for (int i = 0; i < step.getThreadCount(); i++) {
                    executorService.submit(step.getStep());
                }
            }

        }
    }

    public PipelineStatus getStatus() {
        return status;
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
