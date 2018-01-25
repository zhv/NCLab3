import framework.StructuredData;
import framework.pipeline.Pipeline;
import framework.pipeline.PipelineBuilder;
import framework.source.InputJSONSource;
import framework.source.InputXMLSource;
import framework.source.OutputJSONResult;
import framework.source.OutputXMLResult;
import framework.source.Source;
import framework.steps.Step;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        runCustomExample();
//        runJSONExample();
//        runXMLExample();
        runFunctionExample();
    }

    private static void runCustomExample() throws InterruptedException {
        PipelineBuilder pb = new PipelineBuilder();

        pb.addInputStep(new Source() {

            AtomicInteger id = new AtomicInteger();

            @Override
            public void close() throws IOException {

            }

            @Override
            public boolean hasNext() {
                return id.get() < 1000;
            }

            @Override
            public StructuredData next() {
                return new StructuredData(Collections.singletonMap("a", id.getAndIncrement()));
            }
        }, 10);

        pb.addOutputStep(new OutputXMLResult("text1.xml"), 10);

        Pipeline p = pb.getPipeline();
        System.out.println(p);

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        p.start(executorService);
        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);
    }

    private static void runJSONExample() throws InterruptedException {
        PipelineBuilder pb = new PipelineBuilder();

        pb.addInputStep(new InputJSONSource(new ByteArrayInputStream("{ \"root\": [{ \"a\": 1 }, { \"a\": 2 }, { \"a\": 3 }] }".getBytes())), 1);

        pb.addStep(new Step() {
            @Override
            public void run() {
                System.out.println("Run my custom step");
                super.run();
            }

            @Override
            public String toString() {
                return "*" + super.toString();
            }
        }, 3);

//        pb.addForEachStep(x -> x.put("date1", x.getAsDate("date", "d/MM/yyyy").plusYears(5).toString() + " - date"), 1);

        pb.addOutputStep(new OutputXMLResult("text1.xml"), 1);

        Pipeline p = pb.getPipeline();
        System.out.println(p);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        p.start(executorService);
        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);
    }

    private static void runXMLExample() throws InterruptedException {
        PipelineBuilder pb = new PipelineBuilder();


        String data = "<root>\n" +
                "    <a><b>1</b> </a>\n" +
                "    <a><b>2</b></a>\n" +
                "    <a><b>3</b></a>\n" +
                "</root>";
        pb.addInputStep(new InputXMLSource(new ByteArrayInputStream(data.getBytes())), 1);

        pb.addStep(new Step() {
            @Override
            public void run() {
                System.out.println("Run my custom step");
                super.run();
            }
        }, 1);

//        pb.addForEachStep(x -> x.put("date1", x.getAsDate("date", "d/MM/yyyy").plusYears(5).toString() + " - date"), 1);

        pb.addOutputStep(new OutputXMLResult("text1.xml"), 1);

        Pipeline p = pb.getPipeline();
        System.out.println(p);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        p.start(executorService);
        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);
    }

    private static void runFunctionExample() {
        PipelineBuilder pb = new PipelineBuilder();

        pb.addInputStep(new InputJSONSource(null /*...*/), 1);

        Predicate<String> selectKeyA = null;
        Function<String, Integer> convert = Integer::new;

        // via predicate + function
        //pb.addFunction(selectKeyA, convert, 1);

        // specify "A" key explicitly
        //pb.addFunction("A", convert, 1);
        pb.addOutputStep(new OutputJSONResult(null /*..*/), 1);

        // ... ExecutorService ...
    }
}
