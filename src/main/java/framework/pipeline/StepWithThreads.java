package framework.pipeline;

import framework.steps.Step;

class StepWithThreads {

    private final Step step;
    private final int threadCount;

    StepWithThreads(Step step, int threadCount) {
        this.step = step;
        this.threadCount = threadCount;
    }

    public Step getStep() {
        return step;
    }

    public int getThreadCount() {
        return threadCount;
    }

    @Override
    public String toString() {
        return "[x" + threadCount + "]" + step;
    }
}
