import framework.FunctionExceptionAction;
import framework.StructuredData;
import framework.pipeline.Pipeline;
import framework.pipeline.PipelineBuilder;
import framework.source.*;
import framework.steps.Step;
import org.h2.jdbcx.JdbcDataSource;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) throws Exception {
//        runJDBCExample();
        runJSONExample();
//        runXMLExample();
//        runCustomExample();
//        runFunctionExample();
//        runMultiSourceExample();
    }

    private static void runMultiSourceExample() throws InterruptedException, FileNotFoundException  {
        PipelineBuilder pb = new PipelineBuilder();

        String data1 = "{\"root\": " +
                "   [" +
                "       {" +
                "           \"a\": 1" +
                "       }," +
                "       {" +
                "           \"a\": 2" +
                "       }," +
                "       {" +
                "           \"a\": 3" +
                "       }" +
                "   ] " +
                "}";
        String data2 = "{\"root\": " +
                "   [" +
                "       {" +
                "           \"a\": 4" +
                "       }," +
                "       {" +
                "           \"a\": 5" +
                "       }," +
                "       {" +
                "           \"a\": 6" +
                "       }" +
                "   ] " +
                "}";

        pb.addInputStep(new InputJSONSource(new ByteArrayInputStream(data1.getBytes())), 3);
        pb.addInputStep(new InputJSONSource(new ByteArrayInputStream(data2.getBytes())), 3);
        pb.addInputStep(new InputJSONSource(new ByteArrayInputStream(data2.getBytes())), 3);

        pb.addOutputStep(new OutputJSONResult(new FileOutputStream("test1.json")), 5);

        Pipeline p = pb.getPipeline();
        System.out.println(p);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        p.start(executorService);
        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);
    }

    private static void runJDBCExample() throws InterruptedException, ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:default");

        Connection connection = ds.getConnection();
        PreparedStatement createTable = connection.prepareStatement("CREATE TABLE TEST " +
                "(ID INT PRIMARY KEY AUTO_INCREMENT," +
                "AGE INT," +
                "NAME VARCHAR(255));");
        PreparedStatement createTable2 = connection.prepareStatement("CREATE TABLE TEST1 " +
                "(ID INT PRIMARY KEY AUTO_INCREMENT," +
                "AGE INT," +
                "NAME VARCHAR(255));");
        createTable.execute();
        createTable2.execute();
        createTable.close();
        createTable2.close();
        PreparedStatement insert = connection.prepareStatement("INSERT INTO TEST VALUES (1, 10, 'Name1'), (2, 20, 'Name2'), (3, 30, 'Name3')");
        insert.execute();
        insert.close();

        String query1 = "SELECT * FROM TEST";
        PreparedStatementBuilder psb1 = (ps, data) -> { };

        String query2 = "INSERT INTO TEST1 VALUES (?, ?, ?)";
        PreparedStatementBuilder psb2 = (ps, data) -> {
            ps.setInt(1, Integer.parseInt(data.getMap().get("ID").toString()));
            ps.setInt(2, Integer.parseInt(data.getMap().get("AGE").toString()));
            ps.setString(3, data.getMap().get("NAME").toString());
        };

        PipelineBuilder pb = new PipelineBuilder();

        pb.addInputStep(new InputJDBCSource(ds, query1, psb1), 1);
        pb.addOutputStep(new OutputJDBCResult(ds, query2, psb2), 1);

        Pipeline p = pb.getPipeline();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        p.start(executorService);
        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);

        Thread.sleep(2000);

        String query3 = "SELECT * FROM TEST1";
        connection = ds.getConnection();
        PreparedStatement ps3 = connection.prepareStatement(query3);
        ResultSet ds3 = ps3.executeQuery();

        while (ds3.next()) {
            System.out.print("ID: " + ds3.getInt(1) + "; ");
            System.out.print("AGE: " + ds3.getInt(2) + "; ");
            System.out.println("NAME: " + ds3.getString(3) + "; ");
        }

        connection.close();
    }

    private static void runCustomExample() throws InterruptedException, FileNotFoundException {
        PipelineBuilder pb = new PipelineBuilder();

        pb.addInputStep(new Source() {

            AtomicInteger id = new AtomicInteger();
            Integer next = id.get();
            static final int COUNT = 10;

            @Override
            public void close() {
                // nothing to do
            }

            @Override
            public boolean hasNext() {
                return next < COUNT;
            }

            @Override
            public StructuredData next() {
                StructuredData res = new StructuredData(Collections.singletonMap("a", next));
                next = id.getAndIncrement();
                return res;
            }
        }, 10);

        pb.addOutputStep(new OutputXMLResult(new FileOutputStream("test.xml")), 10);

        Pipeline p = pb.getPipeline();
        System.out.println(p);

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        p.start(executorService);
        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);
    }

    private static void runJSONExample() throws InterruptedException, FileNotFoundException {
        PipelineBuilder pb = new PipelineBuilder();

        String data = "{\"root\": " +
                "   [" +
                "       {" +
                "           \"a\": 1" +
                "       }," +
                "       {" +
                "           \"a\": 2" +
                "       }," +
                "       {" +
                "           \"a\": 3" +
                "       }" +
                "   ] " +
                "}";

        pb.addInputStep(new InputJSONSource(new ByteArrayInputStream(data.getBytes())), 1);

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

        pb.addOutputStep(new OutputJSONResult(new FileOutputStream("test1.json")), 1);

        Pipeline p = pb.getPipeline();
        System.out.println(p);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        p.start(executorService);
        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);
    }

    private static void runXMLExample() throws InterruptedException, FileNotFoundException {
        PipelineBuilder pb = new PipelineBuilder();

        String data = "<root>\n" +
                "    <a><b>1</b></a>\n" +
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

        pb.addOutputStep(new OutputXMLResult(new FileOutputStream("test1.xml")), 1);

        Pipeline p = pb.getPipeline();
        System.out.println(p);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        p.start(executorService);
        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);
    }

    private static void runFunctionExample() throws InterruptedException, FileNotFoundException {
        PipelineBuilder pb = new PipelineBuilder();

        String data = "<root>\n" +
                "    <a><b>1</b></a>\n" +
                "    <a><b>2</b></a>\n" +
                "    <a><b>3</b></a>\n" +
                "</root>";

        pb.addInputStep(new InputXMLSource(new ByteArrayInputStream(data.getBytes())), 1);

        Function<Object, Integer> convert = (x) -> {
            System.out.println("Converting: " + x);
            return Integer.parseInt(x.toString());
        };

        pb.addFunction("a", convert, FunctionExceptionAction.STOP_PIPELINE, 1);

        pb.addOutputStep(new OutputJSONResult(new FileOutputStream("test1.json")), 1);

        Pipeline p = pb.getPipeline();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        p.start(executorService);
        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);
    }
}
