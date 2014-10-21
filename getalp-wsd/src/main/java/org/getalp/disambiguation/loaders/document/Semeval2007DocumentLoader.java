package org.getalp.disambiguation.loaders.document;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.LexicalEntry;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Semeval2007DocumentLoader extends DocumentLoader implements ContentHandler {

    private boolean inWord;
    private boolean loadExtra;
    private String currentSurfaceForm;
    private String currentPos;
    private String currentLemma;
    private String currentId;
    private String extraWords;

    private String path;


    public Semeval2007DocumentLoader(String path, boolean loadExtra) {
        inWord = false;
        this.path = path;
        currentId = "";
        currentLemma = "";
        currentPos = "";
        currentSurfaceForm = "";
        extraWords = "";
        this.loadExtra = loadExtra;
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
        if (localName.equals("text")) {
            Document d = new Document();
            d.setId(atts.getValue("id"));
            getDocuments().add(d);

        } else if (localName.equals("instance")) {
            inWord = true;
            currentPos = atts.getValue("pos");
            currentLemma = atts.getValue("lemma");
            currentId = atts.getValue("id");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("corpus")) {

        } else if (localName.equals("text")) {
        } else if (localName.equals("sentence")) {
        } else if (localName.equals("instance")) {
            inWord = false;
            LexicalEntry w = new LexicalEntry(currentId, currentLemma, currentSurfaceForm, currentPos);

            List<String> lextra = new ArrayList<>();
            if (loadExtra) {
                for (String e : extraWords.trim().split("\n")) {
                    if (!e.isEmpty()) {
                        lextra.add(e);
                    }
                }
            }
            w.setPrecedingNonInstances(lextra);
            getDocuments().get(getDocuments().size() - 1).getLexicalEntries().add(w);
            currentId = "";
            currentLemma = "";
            currentPos = "";
            currentSurfaceForm = "";
            extraWords = "";
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inWord) {
            for (int i = start; i < start + length; i++) {
                currentSurfaceForm += ch[i];
            }
        } else {
            for (int i = start; i < start + length; i++) {
                extraWords += ch[i];
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
    public void load() {
        try {
            XMLReader saxReader = XMLReaderFactory.createXMLReader();
            saxReader
                    .setContentHandler(this);
            saxReader.parse(path);
        } catch (IOException | SAXException t) {
            t.printStackTrace();
        }
    }
}
