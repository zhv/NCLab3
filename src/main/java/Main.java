import framework.JavaRDD;
import framework.Pipeline;
import framework.PipelineBuilder;
import framework.source.Result;
import framework.source.Source;

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

    public void main2() {
//        Pipeline pipeline = new Pipeline();
        Source source = null;
        Result result = null;
        PipelineBuilder pipelineBuilder = new PipelineBuilder();
        pipelineBuilder.inputStep(source);
    }
}