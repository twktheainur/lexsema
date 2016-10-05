package org.getalp.lexsema.io.document.loader;

import org.getalp.lexsema.similarity.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

public class WordnetGlossTagCorpusLoader extends CorpusLoaderImpl implements ContentHandler {
    
    private String path;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        processFile(path + "/merged/noun.xml");
        processFile(path + "/merged/adj.xml");
        processFile(path + "/merged/verb.xml");
        processFile(path + "/merged/adv.xml");
    }

    public CorpusLoader loadNonInstances(boolean loadExtra) {
        return this;
    }

    private void processFile(String filePath) {
        System.out.println("[WordnetGlossTag] Processing file " + filePath + "...");
        try {
            saxReader.parse(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        switch (localName) {
        case "wordnet" :
            currentText = new TextImpl();
            inWord = false;
            currentLemma = "";
            currentPos = "";
            currentSurfaceForm = "";
            currentSemanticTag = "";
            break;
        case "synset":
            currentSentence = new SentenceImpl("");
            break;
        case "wf":
            inWord = true;
            currentPos = atts.getValue("pos");
            currentLemma = atts.getValue("lemma");
            if (currentLemma != null && currentLemma.indexOf("%") != -1) {
                currentLemma = currentLemma.substring(0, currentLemma.indexOf("%"));
            }
            break;
        case "id":
            if (inWord && currentSemanticTag.equals("")) {
                currentLemma = atts.getValue("lemma");
                currentSemanticTag = atts.getValue("sk");
                currentSemanticTag = currentSemanticTag.substring(currentSemanticTag.indexOf("%") + 1);
            }
            break;
        }
    }

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
            Word w = new WordImpl("", currentLemma, currentSurfaceForm.trim(), currentPos);
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

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (inWord) {
            for (int i = start; i < start + length; i++) {
                currentSurfaceForm += ch[i];
            }
        }
    }

    public void startDocument() throws SAXException {
        
    }

    public void endDocument() throws SAXException {
        
    }

    public void startPrefixMapping(String arg0, String arg1) throws SAXException {  
        
    }

    public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
        
    }

    public void processingInstruction(String arg0, String arg1) throws SAXException{
        
    }

    public void setDocumentLocator(Locator arg0) {
        
    }

    public void skippedEntity(String arg0) throws SAXException {
        
    }
    
    public void endPrefixMapping(String arg0) throws SAXException {
        
    }

}
