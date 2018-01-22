package framework.source;

import framework.StructuredData;
import org.w3c.dom.Document;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Map;

// todo: close
public class OutputXMLResult implements Result {

    private String path;
    private final String tagName;
    private volatile XMLEventWriter writer;
    private XMLEventFactory factory;

    // todo: formatting - bad way
    private static final String TAB = "\t";
    private static final String END = "\n";

    public OutputXMLResult(String path, String tagName) {
        this.path = path;
        this.tagName = tagName;
    }
    public OutputXMLResult(String path) {
        this.path = path;
        this.tagName = "row";
    }

    @Override
    public void accept(StructuredData data, boolean isDone) {
        if (!isDone) {
            init();

            int index = 1;
            int count = data.size();

            try {
                writer.add(factory.createCharacters(END + TAB));
                writer.add(factory.createStartElement("", "", tagName));
                writer.add(factory.createCharacters(END + TAB + TAB));

                for (Map.Entry<String, Object> e : data.entrySet()){
                    writer.add(factory.createStartElement("", "", e.getKey()));

                    writer.add(factory.createCharacters(e.getValue().toString()));

                    writer.add(factory.createEndElement("", "", e.getKey()));
                    writer.add(factory.createCharacters(END + TAB + (index++ < count ? TAB : "")));
                }

                writer.add(factory.createEndElement("", "", tagName));
                writer.flush();

            } catch (XMLStreamException ignore) { }
        } else {
            try {
                writer.add(factory.createCharacters(END));
                writer.add(this.factory.createEndElement("", "", "root"));
                writer.flush();
            } catch (XMLStreamException ignore) { }
            finally {
                try {
                    writer.close();
                } catch (XMLStreamException ignore) { }
            }
        }
    }

    private synchronized void init() {

        if (writer == null) {
            try {
                OutputStream output = new FileOutputStream(new File(path));
                XMLOutputFactory factory = XMLOutputFactory.newInstance();
                writer = factory.createXMLEventWriter(output);
                this.factory = XMLEventFactory.newFactory();
                writer.add(this.factory.createStartDocument());
                writer.add(this.factory.createCharacters(END));
                writer.add(this.factory.createStartElement("", "", "root"));

            } catch (IOException e) {
                throw new IllegalFileFormatException(e);
            } catch (XMLStreamException ignore) { }
        }
    }

    @Override
    public void close() throws IOException {
        // todo
    }
}
