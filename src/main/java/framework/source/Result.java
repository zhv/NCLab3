package framework.source;

/**
 * @author VYZH
 * @since 16.01.2018
 */
public interface Result<E> {

    void accept(E data);
}
