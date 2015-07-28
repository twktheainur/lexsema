package org.getalp.lexsema.io.document.loader;


import org.getalp.lexsema.similarity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;

@SuppressWarnings({"BooleanParameter", "ClassWithTooManyFields"})
public class SemCorCorpusLoader extends CorpusLoaderImpl implements ContentHandler {

    private Logger logger = LoggerFactory.getLogger(SemCorCorpusLoader.class);

    private boolean inWord;
    private String currentSurfaceForm;
    private String currentPos;
    private String currentLemma;
    private String currentId;
    private String currentSemanticTag;

    private String path;


    private Sentence currentSentence;
    private Text currentDocument;

    public SemCorCorpusLoader(String path) {
        inWord = false;
        this.path = path;
        currentId = "";
        currentLemma = "";
        currentPos = "";
        currentSurfaceForm = "";
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
            case "context":
                currentDocument = new TextImpl();
                currentDocument.setId(atts.getValue("filename"));
                break;
            case "s":
                currentSentence = new SentenceImpl(atts.getValue("snum"));
                break;
            case "wf":
                inWord = true;
                currentPos = atts.getValue("pos");
                currentLemma = atts.getValue("lemma");
                currentId = "";
                currentSemanticTag = atts.getValue("lexsn");
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (localName) {
            case "context":
                addText(currentDocument);
                break;
            case "s":
                currentDocument.addSentence(currentSentence);
                break;
            case "wf":
                inWord = false;
                Word w = new WordImpl(currentId, currentLemma, currentSurfaceForm, currentPos);
                w.setSemanticTag(currentSemanticTag);
                w.setEnclosingSentence(currentSentence);
                currentSentence.addWord(w);
                currentId = "";
                currentLemma = "";
                currentPos = "";
                currentSurfaceForm = "";
                break;
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inWord) {
            for (int i = start; i < start + length; i++) {
                currentSurfaceForm += ch[i];
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
            saxReader.setContentHandler(this);
            saxReader.parse(path);
        } catch (IOException | SAXException t) {
            t.printStackTrace();
            logger.error(t.getLocalizedMessage());
        }
    }

    @Override
    public CorpusLoader loadNonInstances(boolean loadExtra) {
        return this;
    }

}
