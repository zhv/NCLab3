package framework.steps;

public class OutputStep<E> extends Step<E> {



    @Override
    public boolean hasNext() {
        return prev.hasNext();
    }

    @Override
    public E next() {
        return null;
    }
}
