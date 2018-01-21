package framework.source;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import framework.StructuredData;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class InputJSONSource implements Source {

    private String path;
    private String datePattern;
    private volatile JsonParser jsonParser;

    public InputJSONSource(String path) {
        this.path = path;
    }
    public InputJSONSource(String path, String datePattern) {
        this.path = path;
        this.datePattern = datePattern;
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

                if (jsonParser.currentToken() == JsonToken.START_ARRAY
                        || jsonParser.currentToken() == JsonToken.START_OBJECT
                        || jsonParser.currentToken() == JsonToken.NOT_AVAILABLE){
                    throw new IllegalArgumentException("Illegal file format!");
                }

                String name = jsonParser.getCurrentName();
                jsonParser.nextToken();
                Object value = null;

                if (datePattern == null) {
                    try {
                        LocalDate.parse(jsonParser.getValueAsString());
                        value = LocalDate.parse(jsonParser.getValueAsString());
                    } catch (Exception ignore) { }
                } else {
                    try {
                        LocalDate.parse(jsonParser.getValueAsString(), DateTimeFormatter.ofPattern(datePattern));
                        value = LocalDate.parse(jsonParser.getValueAsString(), DateTimeFormatter.ofPattern(datePattern));
                    } catch (Exception ignore) { }
                }


                if (value == null) {
                    if (jsonParser.getCurrentToken().isNumeric()) {
                        try {
                            value = jsonParser.getValueAsInt();
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
            return new StructuredData(row);
        } catch (IOException e) {
            throw new IllegalFileFormatException(e);
        }
    }

    private synchronized void init() {
        if (jsonParser == null) {
            try {
                JsonFactory jsonFactory = new JsonFactory();
                jsonParser = jsonFactory.createParser(new File(path));
                while (jsonParser.nextToken() != JsonToken.START_ARRAY) { }
                jsonParser.nextToken();
            } catch (IOException e) {
                throw new IllegalFileFormatException(e);
            }
        }
    }
}
