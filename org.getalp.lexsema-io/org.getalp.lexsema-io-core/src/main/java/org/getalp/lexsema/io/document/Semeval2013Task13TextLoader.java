package org.getalp.lexsema.io.document;


import org.getalp.lexsema.similarity.*;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"BooleanParameter", "ClassWithTooManyFields"})
public class Semeval2013Task13TextLoader extends TextLoaderImpl implements ContentHandler {

    private Logger logger = LoggerFactory.getLogger(Semeval2013Task13TextLoader.class);

    private boolean inWord;
    private String currentSurfaceForm;
    private String currentPos;
    private String currentLemma;
    private String currentId;
    private Language language;

    private String path;


    private List<Word> currentPrecedingWords;
    private Sentence currentSentence;
    private Text currentDocument;

    public Semeval2013Task13TextLoader(String path) {
        inWord = false;
        this.path = path;
        currentId = "";
        currentLemma = "";
        currentPos = "";
        currentSurfaceForm = "";
        currentPrecedingWords = new ArrayList<>();
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
            case "corpus":
                language = Language.fromCode(atts.getValue("lang"));
                break;
            case "text":
                currentDocument = new TextImpl(language);
                currentDocument.setId(atts.getValue("id"));
                break;
            case "sentence":
                currentSentence = new SentenceImpl(atts.getValue("id"));
                currentSentence.setLanguage(language);
                break;
            case "wf":
                currentPos = atts.getValue("pos");
                currentLemma = atts.getValue("lemma");
                currentId = "";
                currentPrecedingWords.add(new WordImpl("non-target", currentLemma, currentSurfaceForm, currentPos));
                inWord = true;
                break;
            case "instance":
                inWord = true;
                currentPos = atts.getValue("pos");
                currentLemma = atts.getValue("lemma");
                currentId = atts.getValue("id");
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (localName) {
            case "text":
                addText(currentDocument);
                break;
            case "sentence":
                currentDocument.addSentence(currentSentence);
                break;
            case "wf":
                inWord = false;
                currentSurfaceForm = "";
                currentId = "";
                currentLemma = "";
                currentPos = "";
                break;
            case "instance":
                inWord = false;
                Word w = new WordImpl(currentId, currentLemma, currentSurfaceForm, currentPos);
                for (Word pw : currentPrecedingWords) {
                    w.addPrecedingInstance(pw);
                }
                currentPrecedingWords.clear();
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
            saxReader
                    .setContentHandler(this);
            saxReader.parse(path);
        } catch (IOException | SAXException t) {
            t.printStackTrace();
            logger.error(t.getLocalizedMessage());
        }
    }

    @Override
    public TextLoader loadNonInstances(boolean loadExtra) {
        return this;
    }

}
