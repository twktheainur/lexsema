package org.getalp.lexsema.io.clwsd;

import edu.stanford.nlp.util.Pair;
import org.getalp.lexsema.io.text.EnglishDKPTextProcessor;
import org.getalp.lexsema.io.text.TextProcessor;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.WordImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.util.Iterator;

@SuppressWarnings({"BooleanParameter", "ClassWithTooManyFields"})
public class Semeval2013Task10EntryLoader implements ContentHandler, TargetEntryLoader {

    private Logger logger = LoggerFactory.getLogger(Semeval2013Task10EntryLoader.class);

    private String path;
    private TextProcessor textProcessor;

    private boolean inContext = false;
    private int numberOfSpacesInContext;
    private String contextString = "";

    private TargetWordEntry entry;
    private String targetWordId = "";
    private int currentContextId;
    private int currentWordIndex;
    private boolean inHead;


    public Semeval2013Task10EntryLoader(String path) {
        this.path = path;
        textProcessor = new EnglishDKPTextProcessor();
    }

    @Override
    public void setDocumentLocator(Locator locator) {

    }

    @Override
    public void startDocument() throws SAXException {

    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {

    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        switch (localName) {
            case "lexelt":
                targetWordId = atts.getValue("item");
                String lemma = targetWordId.split("\\.")[0];
                String pos = targetWordId.split("\\.")[1];
                entry = new TargetWordEntryImpl(new WordImpl(targetWordId, lemma, lemma, pos));
                logger.info(String.format("Loading %s ...", targetWordId));
                break;
            case "instance":
                contextString = "";
                currentContextId = Integer.valueOf(atts.getValue("id"));
                logger.info(String.format("Loading context %d ...", currentContextId));
                break;
            case "context":
                inContext = true;
                currentWordIndex = 0;
                numberOfSpacesInContext = 0;
                break;
            case "head":
                inHead = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (localName) {
            case "context":
                inContext = false;
                //contextString = contextString.replaceAll("\\p{Punct}","");
                contextString = contextString.replaceAll("<[/]?head>", "");
                contextString = contextString.replaceAll("/", "_");
                try {
                    Text s = textProcessor.process(contextString, targetWordId + "#" + currentContextId);
                    entry.addContext(s, currentWordIndex);
                } catch (RuntimeException e) {
                    logger.warn(e.getLocalizedMessage());
                    e.printStackTrace();
                }
                break;
            case "head":
                currentWordIndex = numberOfSpacesInContext;
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inContext) {
            for (int i = start; i < start + length; i++) {
                if (ch[i] == ' ') {
                    numberOfSpacesInContext++;
                }
                contextString += ch[i];
            }
            if (inHead) {

            }
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {

    }

    @Override
    public void skippedEntity(String name) throws SAXException {

    }

    @Override
    public TargetWordEntry load() {
        try {
            XMLReader saxReader = XMLReaderFactory.createXMLReader();
            saxReader
                    .setContentHandler(this);
            saxReader.parse(path);
            return entry;
        } catch (IOException | SAXException t) {
            logger.error(t.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public Iterator<Pair<Text, Integer>> iterator() {
        return entry.iterator();
    }

    public TargetWordEntry getEntry() {
        return entry;
    }
}
