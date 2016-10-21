package org.getalp.lexsema.io.document.loader;


import org.getalp.lexsema.similarity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;

public class Semeval2007CorpusLoader extends CorpusLoaderImpl implements ContentHandler {

    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.DEFAULT_DOCUMENT_FACTORY;
    private static final Logger logger = LoggerFactory.getLogger(Semeval2007CorpusLoader.class);

    private boolean inWord;
    private boolean loadExtra;
    private String currentSurfaceForm;
    private String currentPos;
    private String currentLemma;
    private String currentId;
    private String extraWords;

    private InputStream inputStream;

    private Sentence currentSentence;
    private Text currentDocument;

    public Semeval2007CorpusLoader(InputStream inputStream) {
        this.inputStream = inputStream;
        init();
    }

    public Semeval2007CorpusLoader(String path) {
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
        init();
    }

    private void init() {
        inWord = false;
        currentId = "";
        currentLemma = "";
        currentPos = "";
        currentSurfaceForm = "";
        extraWords = "";
    }
    
    private void addExtraWords() {
        for (String e : extraWords.trim().split(System.getProperty("line.separator"))) {
            if (!e.isEmpty()) {
             	Word wextra = DOCUMENT_FACTORY.createWord("non-target", "", e, "");
            	wextra.setEnclosingSentence(currentSentence);
            	currentSentence.addWord(wextra);
            }
        }
        extraWords = "";
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
            case "text":
                currentDocument = DOCUMENT_FACTORY.createText();
                currentDocument.setId(atts.getValue("id"));
                break;
            case "sentence":
                currentSentence = DOCUMENT_FACTORY.createSentence(atts.getValue("id"));
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
                if (loadExtra) {
                	addExtraWords();
                }

                break;
            case "instance":
                inWord = false;
                Word w = DOCUMENT_FACTORY.createWord(currentId, currentLemma, currentSurfaceForm, currentPos);

                if (loadExtra) {
                	addExtraWords();
                }

                w.setEnclosingSentence(currentSentence);
                currentSentence.addWord(w);
                currentId = "";
                currentLemma = "";
                currentPos = "";
                currentSurfaceForm = "";
                extraWords = "";
                break;
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
            clearTexts();
            init();
            XMLReader saxReader = XMLReaderFactory.createXMLReader();
            saxReader.setContentHandler(this);
            saxReader.setEntityResolver(new EntityResolverIgnoringDTD());
            saxReader.parse(new InputSource(inputStream));
        } catch (IOException | SAXException t) {
            logger.error(t.getLocalizedMessage());
        }
    }


    @Override
    public CorpusLoader loadNonInstances(boolean loadExtra) {
        this.loadExtra = loadExtra;
        return this;
    }

    private static class EntityResolverIgnoringDTD implements EntityResolver {
        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            if (systemId.contains("coarse-all-words.dtd")) {
                return new InputSource(new StringReader(""));
            } else {
                return null;
            }
        }
    }
}
