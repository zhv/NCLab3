import framework.pipeline.Pipeline;
import framework.pipeline.PipelineBuilder;
import framework.source.InputJSONSource;
import framework.source.OutputXMLResult;

public class Main {
    public static void main(String[] args) {

        PipelineBuilder pb = new PipelineBuilder();

        pb.addInputStep(new InputJSONSource("text.json"), 1);

        pb.addForEachStep(x -> x.put("date1", x.getAsDate("date", "d/MM/yyyy").plusYears(5).toString() + " - date"), 1);

        pb.addOutputStep(new OutputXMLResult("text1.xml"), 1);

        Pipeline p = pb.getPipeline();

        p.start();

    }
}