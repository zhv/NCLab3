import framework.JavaRDD;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        JavaRDD<Integer> j1 = JavaRDD.parallelize(3,1,2,2);
        JavaRDD<Integer> j2 = JavaRDD.parallelize(1,2,3,2,2);

        System.out.println(j1);
        System.out.println(j2);
        System.out.println(j1.intersection(j2));
    }
}