package org.getalp.lexsema.io.clwsd;


import org.getalp.lexsema.similarity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"BooleanParameter", "ClassWithTooManyFields"})
public class Semeval2013Task10EntryLoader implements ContentHandler {

    private Logger logger = LoggerFactory.getLogger(Semeval2013Task10EntryLoader.class);

    private boolean inContext = false;
    private boolean inHead = false;
    String contextString = "";

    private String path;

    private TargetWordEntry entry;

    public Semeval2013Task10EntryLoader(String path) {
        this.path = path;
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
            case "lexlt":
                String item = atts.getValue("item");
                String lemma= item.split("\\.")[0];
                String pos = item.split("\\.")[1];
                entry = new TargetWordEntryImpl(new WordImpl(item,lemma,lemma,pos));
                break;
            case "context":
                inContext = true;
                break;
            case "head":
                    inHead = true;
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (localName) {
            case "context":
                inContext = false;
                contextString = contextString.replaceAll("\\p{Punct}","");
                List<String> context = new ArrayList<>();
                String [] token = contextString.split(" ");
                int cIndex;
                int targetWordIndex = 0;
                for(cIndex =0; cIndex <token.length; cIndex++){
                    if(token[cIndex].startsWith("<head>")){
                        targetWordIndex = cIndex;
                    } else {

                    }
                }
                contextString = "";
            case "head":
                inHead = false;
                break;
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inContext) {
            for (int i = start; i < start + length; i++) {
                contextString += ch[i];
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

    public void load() {
        try {
            XMLReader saxReader = XMLReaderFactory.createXMLReader();
            saxReader
                    .setContentHandler(this);
            saxReader.parse(path);
        } catch (IOException | SAXException t) {
            logger.error(t.getLocalizedMessage());
        }
    }



}
