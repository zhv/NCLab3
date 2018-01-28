package framework.source;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import framework.StructuredData;

import java.io.*;
import java.util.Map;

public class OutputJSONResult implements Result {

    private OutputStream output;
    private volatile JsonGenerator writer;

    public OutputJSONResult(OutputStream output) {
        this.output = output;
    }

    @Override
    public synchronized void accept(StructuredData data) {
        init();

        try {
            writer.writeStartObject();

            for (Map.Entry<String, Object> e : data.getMap().entrySet()) {
                if (e.getValue() instanceof Double) {
                    writer.writeNumberField(e.getKey(), (Double) e.getValue());
                } else if (e.getValue() instanceof Float) {
                    writer.writeNumberField(e.getKey(), (Float) e.getValue());
                } else if (e.getValue() instanceof Long) {
                    writer.writeNumberField(e.getKey(), (Long) e.getValue());
                } else if (e.getValue() instanceof Integer) {
                    writer.writeNumberField(e.getKey(), (Integer) e.getValue());
                } else if (e.getValue() instanceof Boolean) {
                    writer.writeBooleanField(e.getKey(), (Boolean) e.getValue());
                } else if (e.getValue() == null) {
                    writer.writeNullField(e.getKey());
                } else {
                    writer.writeStringField(e.getKey(), e.getValue().toString());
                }
            }

            writer.writeEndObject();

            writer.flush();
        } catch (IOException e) {
            throw new IllegalFileFormatException(e);
        }

        if (data.setLast()) {
            close();
        }
    }

    private synchronized void init() {

        if (writer == null) {
            try {
                JsonFactory jsonFactory = new JsonFactory();
                writer = jsonFactory.createGenerator(output);
                writer.writeStartObject();
                writer.writeFieldName("root");
                writer.writeStartArray();
            } catch (IOException e) {
                throw new IllegalFileFormatException(e);
            }
        }
    }

    @Override
    public void close() {
        try {
            writer.writeEndArray();
            writer.writeEndObject();
        } catch (IOException e) {
            throw new IllegalFileFormatException(e);
        } finally {
            try {
                writer.close();
                writer = null;
            } catch (IOException ignore) { }
        }
    }
}
