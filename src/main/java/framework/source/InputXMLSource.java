package framework.source;

import framework.StructuredData;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class InputXMLSource implements Source {

    private final InputStream input;
    private volatile XMLEventReader reader;
    private String prevStartElement;
    private String rowStartElement;
    private XMLEvent event;
    private StructuredData next;
    private final XMLInputFactory factory;

    public InputXMLSource(InputStream input) {
        this(input, XMLInputFactory.newFactory());
    }

    public InputXMLSource(InputStream input, XMLInputFactory factory) {
        this.input = input;
        this.factory = factory;
    }

    @Override
    public synchronized boolean hasNext() {
        init();

        return next != null;
    }

    @Override
    public synchronized StructuredData next() {
        init();

        String startElement = null;
        Characters chars;
        int eventType;

        Map<String, Object> row = new LinkedHashMap<>();

        if (rowStartElement != null)
            while (reader.hasNext()) {
                try {
                    event = reader.nextEvent();
                } catch (XMLStreamException e) {
                    throw new IllegalStateException(e);
                }
                if (event.getEventType() == XMLStreamConstants.START_ELEMENT && event.asStartElement().getName().toString().equalsIgnoreCase(rowStartElement)) {
                    break;
                }
            }

        while (reader.hasNext()) {
            eventType = event.getEventType();

            if (eventType == XMLStreamConstants.START_ELEMENT) {

                prevStartElement = startElement;
                startElement = event.asStartElement().getName().toString();

            } else if (eventType == XMLStreamConstants.END_ELEMENT) {
                if (event.asEndElement().getName().toString().equalsIgnoreCase(rowStartElement)) {
                    return new StructuredData(row);
                }
            } else if (eventType == XMLStreamConstants.CHARACTERS) {

                chars = event.asCharacters();

                if (!chars.isWhiteSpace())
                {
                    if (rowStartElement == null) rowStartElement = prevStartElement;

                    row.put(startElement, chars.getData());
                }
            }

            try {
                event = reader.nextEvent();
            } catch (XMLStreamException ignore) { }
        }

        StructuredData sd = new StructuredData(next.getMap());

        if (row.isEmpty()) {
            next = null;
        }
        else {
            next = new StructuredData(row);
        }

        return sd;
    }

    private synchronized void init() {
        if (reader == null) {
            try {
                reader = factory.createXMLEventReader(input);
                if (reader.hasNext())
                    event = reader.nextEvent();
                next = next();
            } catch (XMLStreamException ignore) { }
        }
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
                input.close();
                reader = null;
            } catch (XMLStreamException | IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
