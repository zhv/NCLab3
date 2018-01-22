package framework.source;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import framework.StructuredData;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class OutputJSONResult implements Result {

    private String path;
    private volatile JsonGenerator writer;
    private static final String TAB = "\t";
    private static final String END = "\n";

    public OutputJSONResult(String path) {
        this.path = path;
    }

    @Override
    public void accept(StructuredData data, boolean isDone) {
        if (!isDone){
            init();

            try {
                writer.writeRaw(TAB);
                writer.writeStartObject();
                writer.writeRaw(END + TAB + TAB);

                for (Map.Entry<String, Object> e : data.entrySet()) {
                    if (e.getValue() instanceof Double) {
                        writer.writeNumberField(e.getKey(), (Double) e.getValue());
                    } else if (e.getValue() instanceof Integer) {
                        writer.writeNumberField(e.getKey(), (Integer) e.getValue());
                    } else {
                        writer.writeStringField(e.getKey(), e.getValue().toString());
                    }
                    writer.writeRaw(END + TAB + TAB);
                }

                writer.writeEndObject();
                writer.writeRaw(END + TAB);

                writer.flush();
            } catch (IOException e) {
                throw new IllegalFileFormatException(e);
            }
        } else {
            try {
                writer.writeEndArray();
                writer.writeRaw(END);
                writer.writeEndObject();
            } catch (IOException e) {
                throw new IllegalFileFormatException(e);
            } finally {
                try {
                    writer.close();
                } catch (IOException ignore) { }
            }

        }
    }

    private synchronized void init() {

        if (writer == null) {
            try {
                OutputStream output = new FileOutputStream(new File(path));
                JsonFactory jsonFactory = new JsonFactory();
                writer = jsonFactory.createGenerator(output);
                writer.writeStartObject();
                writer.writeRaw(END + TAB);
                writer.writeFieldName("root");
                writer.writeRaw(END + TAB);
                writer.writeStartArray();
                writer.writeRaw(END + TAB);
            } catch (IOException e) {
                throw new IllegalFileFormatException(e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        // todo
    }
}
