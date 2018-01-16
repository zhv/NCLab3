package framework.steps;

import java.util.Iterator;

/**
 * @author VYZH
 * @since 11.01.2018
 */
public abstract class Step<E> implements Iterator<E> {
    protected Step<E> prev;
}
