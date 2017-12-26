package framework;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unchecked")
public class JavaRDD<T> {

    private T[] dataSet;

    @SafeVarargs
    private JavaRDD(T... data) {
        dataSet = data.clone();
    }

    @SafeVarargs
    public static <E> JavaRDD<E> parallelize(E... data) {
        return new JavaRDD<>(data);
    }

    public static <E> JavaRDD<E> parallelize(Collection<E> data) {
        return new JavaRDD<>((E[]) data.toArray());
    }

    public static <E> JavaRDD<E> parallelize(JavaRDD<E> data) {
        return new JavaRDD<>((E[]) data.collect());
    }

    public static JavaRDD<String> textFile(String path) throws IOException {
        return new JavaRDD<>(Files.lines(Paths.get(path)).toArray(String[]::new));
    }

    public <R> JavaRDD<R> map(Function<? super T, ? extends R> function) {

        Object[] newDataSet = new Object[dataSet.length];

        for (int i = 0; i < dataSet.length; i++)
            newDataSet[i] = function.apply(dataSet[i]);

        return new JavaRDD<>((R[]) newDataSet);
    }

    public <R> JavaRDD<R> flatMap(Function<? super T, ? extends JavaRDD<? extends R>> function) {

        List<R> newDataSet = new ArrayList<>();

        for (T t : dataSet)
            function.apply(t).forEach(newDataSet::add);

        return new JavaRDD<>((R[]) newDataSet.toArray());
    }

    public JavaRDD<T> filter(Predicate<? super T> predicate) {

        List<T> data = new ArrayList<>(Arrays.asList(dataSet));
        data.removeIf(predicate);
        Object[] newDataSet = data.toArray();

        return new JavaRDD<>((T[]) newDataSet);
    }

    public void forEach(Consumer<T> consumer) {
        for (T t : dataSet)
            consumer.accept(t);
    }

    public JavaRDD<T> distinct() {

        Object[] newDataSet = new HashSet<>(Arrays.asList(dataSet)).toArray();

        return new JavaRDD<>((T[]) newDataSet);
    }

    public JavaRDD<T> union(JavaRDD<T> javaRDD) {

        int i = 0;
        int len = dataSet.length + javaRDD.dataSet.length;
        Object[] data = new Object[len];

        for (int j = 0; j < dataSet.length; j++, i++)
            data[i] = dataSet[j];

        for (int j = 0; j < javaRDD.dataSet.length; j++, i++)
            data[i] = javaRDD.dataSet[j];

        return new JavaRDD<>((T[]) data);
    }

    public JavaRDD<T> intersection(JavaRDD<T> javaRDD) {

        Object[] tmp = new Object[dataSet.length > javaRDD.dataSet.length ? javaRDD.dataSet.length : dataSet.length];
        int len = 0;
        Set<Integer> set = IntStream.range(0, javaRDD.dataSet.length).boxed().collect(Collectors.toSet());
        Iterator<Integer> setIterator = set.iterator();

        for (T t : dataSet)
            while (setIterator.hasNext())
                if (t.equals(javaRDD.dataSet[setIterator.next()])) {
                    setIterator.remove();
                    setIterator = set.iterator();
                    tmp[len] = t;
                    len++;
                    break;
                }

        Object[] newDataSet = new Object[len];
        System.arraycopy(tmp, 0, newDataSet, 0, len);

        return new JavaRDD<>((T[]) newDataSet);
    }

    //unsupported
    public JavaRDD<T> cartesian(JavaRDD<T> javaRDD) {

        JavaPairRDD<T, T> data;

        return null;
    }

    public void saveAsTextFile(String path) {

    }

    public Object[] collect() {
        return dataSet.clone();
    }

    public Object[] take(int count) {

        if (dataSet.length < count)
            throw new IllegalArgumentException();

        Object[] tmp = new Object[count];

        System.arraycopy(dataSet, 0, tmp, 0, count);

        return tmp;
    }

    public int count() {
        return dataSet.length;
    }

    public T reduce(BiFunction<T, T, T> function) {
        T tmp = dataSet[0];

        for (int i = 1; i < dataSet.length; i++)
            tmp = function.apply(tmp, dataSet[i]);

        return tmp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < dataSet.length - 1; i++)
            sb.append(dataSet[i].toString()).append(", ");

        if (dataSet.length > 0)
            sb.append(dataSet[dataSet.length - 1].toString());

        return sb.append(']').toString();
    }
}
