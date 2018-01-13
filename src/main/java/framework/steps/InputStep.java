package framework.steps;

import framework.source.Source;

/**
 * @author VYZH
 * @since 11.01.2018
 */
public class InputStep<E> implements Step<E> {

    private Source<E> source;

    public InputStep(Source<E> source) {
        this.source = source;
    }

    @Override
    public boolean hasNext() {
        return source.hasNext();
    }

    @Override
    public E next() {
        return source.next();
    }
}
