package framework.steps;

public abstract class TransformStructureStep<I, O> extends Step<O> {
    protected Step<I> prev;
}
