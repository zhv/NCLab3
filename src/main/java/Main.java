import framework.StructuredData;
import framework.pipeline.Pipeline;
import framework.pipeline.PipelineBuilder;
import framework.source.InputJSONSource;
import framework.source.InputXMLSource;
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

public class Main {

    public static void main(String[] args) throws InterruptedException {
        runCustomExample();
//        runJSONExample();
//        runXMLExample();
    }

    private static void runCustomExample() throws InterruptedException {
        PipelineBuilder pb = new PipelineBuilder();

        pb.addInputStep(new Source() {

            Queue<StructuredData> data = new ConcurrentLinkedQueue<>(Arrays.asList(
                    new StructuredData(Collections.singletonMap("a", 1)),
                    new StructuredData(Collections.singletonMap("a", 2))
            ));

            @Override
            public void close() throws IOException {

            }

            @Override
            public boolean hasNext() {
                return !data.isEmpty();
            }

            @Override
            public StructuredData next() {
                return data.poll();
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

    private static void runJSONExample() throws InterruptedException {
        PipelineBuilder pb = new PipelineBuilder();

        pb.addInputStep(new InputJSONSource(new ByteArrayInputStream("{ \"a\": [1, 2, 3] }".getBytes())), 1);

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

    private static void runXMLExample() throws InterruptedException {
        PipelineBuilder pb = new PipelineBuilder();

        pb.addInputStep(new InputXMLSource(new ByteArrayInputStream("<a><b>1</b><b>2</b><b>3</b></a>".getBytes())), 1);

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
}