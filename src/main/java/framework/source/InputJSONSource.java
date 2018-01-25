package framework.source;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import framework.StructuredData;

import java.io.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class InputJSONSource implements Source {

    private volatile JsonParser jsonParser;
    private final InputStream input;

    public InputJSONSource(InputStream input) {
        this.input = input;
    }

    @Override
    public synchronized boolean hasNext() {
        init();

        return jsonParser.getCurrentToken() != JsonToken.END_ARRAY;
    }

    @Override
    public synchronized StructuredData next() {
        init();

        Map<String, Object> row = new LinkedHashMap<>();
        try {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {

                String name = jsonParser.getCurrentName();
                jsonParser.nextToken();
                Object value = null;

                try {
                    LocalDate.parse(jsonParser.getValueAsString());
                    value = LocalDate.parse(jsonParser.getValueAsString());
                } catch (Exception ignore) { }

                if (value == null) {
                    if (jsonParser.getCurrentToken().isNumeric()) {
                        try {
                            value = jsonParser.getValueAsLong();
                        } catch (Exception ignore) { }

                        if (value == null) value = jsonParser.getValueAsDouble();
                    } else {
                        value = jsonParser.getValueAsString();
                    }
                }

                if (value != null) {
                    row.put(name, value);
                }
            }
            jsonParser.nextToken();

            StructuredData sd = new StructuredData(row);
            sd.isLast(jsonParser.getCurrentToken() == JsonToken.END_ARRAY);

            return sd;
        } catch (IOException | NullPointerException e) {
            throw new IllegalFileFormatException(e);
        }
    }

    private synchronized void init() {
        if (jsonParser == null) {
            try {
                JsonFactory jsonFactory = new JsonFactory();
                jsonParser = jsonFactory.createParser(input);
                while (jsonParser.nextToken() != JsonToken.START_ARRAY) { }
                jsonParser.nextToken();
            } catch (IOException e) {
                throw new IllegalFileFormatException(e);
            }
        }
    }

    @Override
    public void close() {
        try {
            jsonParser.close();
            input.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
