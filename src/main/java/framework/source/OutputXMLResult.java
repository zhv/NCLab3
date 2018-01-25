package framework.source;

import framework.StructuredData;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Map;

public class OutputXMLResult implements Result {

    private OutputStream output;
    private volatile XMLEventWriter writer;
    private XMLEventFactory factory;

    public OutputXMLResult(OutputStream output) {
        this.output = output;
    }

    @Override
    public synchronized void accept(StructuredData data) {
        init();

        try {
            writer.add(factory.createStartElement("", "", "row"));

            for (Map.Entry<String, Object> e : data.getMap().entrySet()){
                writer.add(factory.createStartElement("", "", e.getKey()));

                writer.add(factory.createCharacters(e.getValue() == null ? "null" : e.getValue().toString()));

                writer.add(factory.createEndElement("", "", e.getKey()));
            }

            writer.add(factory.createEndElement("", "", "row"));
            writer.flush();

        } catch (XMLStreamException ignore) { }

        if (data.isLast()) {
            close();
        }
    }

    private synchronized void init() {

        if (writer == null) {
            try {
                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                writer = factory.createXMLEventWriter(output);
                this.factory = XMLEventFactory.newFactory();
                writer.add(this.factory.createStartDocument());
                writer.add(this.factory.createStartElement("", "", "root"));

            } catch (XMLStreamException ignore) { }
        }
    }

    @Override
    public void close() {
        try {
            writer.add(this.factory.createEndElement("", "", "root"));
            writer.flush();
        } catch (XMLStreamException ignore) { }
        finally {
            try {
                writer.close();
                writer = null;
            } catch (XMLStreamException ignore) { }
        }
    }
}
