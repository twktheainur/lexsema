package org.getalp.lexsema.io.document.loader;

import org.getalp.lexsema.similarity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.text.MessageFormat;

public class WordnetGlossTagCorpusLoader extends CorpusLoaderImpl implements ContentHandler {

    private static final Logger logger = LoggerFactory.getLogger(WordnetGlossTagCorpusLoader.class);
    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.DEFAULT;

    private final String path;

    private XMLReader saxReader;
    
    private Text currentText;
    
    private Sentence currentSentence;
    
    private boolean inWord;
    
    private String currentLemma;
    
    private String currentPos;
    
    private String currentSurfaceForm;
    
    private String currentSemanticTag;


    
    public WordnetGlossTagCorpusLoader(String path) {
        this.path = path;
        try {
            saxReader = XMLReaderFactory.createXMLReader();
            saxReader.setContentHandler(this);
        } catch (SAXException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void load() {
        processFile(MessageFormat.format("{0}/merged/noun.xml", path));
        processFile(MessageFormat.format("{0}/merged/adj.xml", path));
        processFile(MessageFormat.format("{0}/merged/verb.xml", path));
        processFile(MessageFormat.format("{0}/merged/adv.xml", path));
    }

    @Override
    public CorpusLoader loadNonInstances(boolean loadExtra) {
        return this;
    }

    private void processFile(String filePath) {
        logger.info("[WordnetGlossTag] Processing file {}...", filePath);
        try {
            saxReader.parse(filePath);
        } catch (SAXException|IOException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        switch (localName) {
        case "wordnet" :
            currentText = DOCUMENT_FACTORY.createText();
            inWord = false;
            currentLemma = "";
            currentPos = "";
            currentSurfaceForm = "";
            currentSemanticTag = "";
            break;
        case "synset":
            currentSentence = DOCUMENT_FACTORY.createSentence("");
            break;
        case "wf":
            inWord = true;
            currentPos = atts.getValue("pos");
            currentLemma = atts.getValue("lemma");
            if (currentLemma != null && currentLemma.contains("%")) {
                currentLemma = currentLemma.substring(0, currentLemma.indexOf("%"));
            }
            break;
        case "id":
            if (inWord && currentSemanticTag.isEmpty()) {
                currentLemma = atts.getValue("lemma");
                currentSemanticTag = atts.getValue("sk");
                currentSemanticTag = currentSemanticTag.substring(currentSemanticTag.indexOf("%") + 1);
            }
            break;
        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (localName) {
        case "wordnet" :
            addText(currentText);
            break;
        case "synset":
            currentText.addSentence(currentSentence);
            break;
        case "wf":
            currentSurfaceForm = currentSurfaceForm.trim();
            Word w = DOCUMENT_FACTORY.createWord("", currentLemma, currentSurfaceForm.trim(), currentPos);
            w.setSemanticTag(currentSemanticTag);
            w.setEnclosingSentence(currentSentence);
            currentSentence.addWord(w);
            inWord = false;
            currentLemma = "";
            currentPos = "";
            currentSurfaceForm = "";
            currentSemanticTag = "";
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
    public void startDocument() throws SAXException {
        
    }

    @Override
    public void endDocument() throws SAXException {
        
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException{
        
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        
    }

}
