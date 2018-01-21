package framework.source;

import framework.StructuredData;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.LinkedHashMap;
import java.util.Map;

public class InputXMLSource implements Source {

    private String path;
    private volatile XMLEventReader reader;
    private String prevStartElement;
    private String rowStartElement;
    private XMLEvent event;
    private StructuredData next;

    public InputXMLSource(String path) {
        this.path = path;
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

        Map<String, Object> tmp = new LinkedHashMap<>(next.getMap());

        if (row.isEmpty()) next = null;
        else next = new StructuredData(row);

        return new StructuredData(tmp);
    }

    private synchronized void init() {
        if (reader == null) {
            try {
                InputStream input = new FileInputStream(new File(path));
                XMLInputFactory factory = XMLInputFactory.newInstance();
                reader = factory.createXMLEventReader(input);
                if (reader.hasNext())
                    event = reader.nextEvent();
                next = next();
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException(e);
            } catch (XMLStreamException ignore) { }
        }
    }
}
