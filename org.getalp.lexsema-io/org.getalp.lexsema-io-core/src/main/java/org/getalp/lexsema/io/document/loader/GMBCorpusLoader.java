package org.getalp.lexsema.io.document.loader;


import java.io.File;

import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.SentenceImpl;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.TextImpl;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.WordImpl;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

public class GMBCorpusLoader extends CorpusLoaderImpl implements ContentHandler {
    
    private String path;

    private Dictionary wordnet;
    
    private XMLReader saxReader;
    
    private Text currentText;
    
    private Sentence currentSentence;
    
    private boolean inSurfaceForm;
    
    private boolean inPos;
    
    private boolean inLemma;
    
    private boolean inSenseID;

    private String currentSurfaceForm;

    private String currentPos;
    
    private String currentLemma;
    
    private String currentSenseID;
    
    public GMBCorpusLoader(String path, Dictionary wordnet) {
        this.path = path;
        this.wordnet = wordnet;
        try {
            saxReader = XMLReaderFactory.createXMLReader();
            saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            saxReader.setContentHandler(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        openWordnet();
        File data = new File(path + "/data/");
        for (String subFolder : data.list()) {
            processDirectory(path + "/data/" + subFolder);
        }
    }

    public CorpusLoader loadNonInstances(boolean loadExtra) {
        return this;
    }

    private void processDirectory(String path) {
        System.out.println("[GMB] Processing directory " + path + "...");
        File dir = new File(path);
        for (String subFolder : dir.list()) {
            processFile(path + "/" + subFolder);
        }
    }
    
    private void processFile(String filePath) {
        // System.out.println("[GMB] Processing file " + filePath + "...");
        try {
            saxReader.parse(filePath + "/en.drs.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        switch (localName) {
        case "taggedtokens" :
            currentText = new TextImpl();
            currentSentence = new SentenceImpl("");
            inSurfaceForm = false;
            inPos = false;
            inLemma = false;
            inSenseID = false;
            currentSurfaceForm = "";
            currentPos = "";
            currentLemma = "";
            currentSenseID = "";
            break;
        case "tagtoken":
            break;
        case "tag" :
            String type = atts.getValue("type");
            switch (type) {
            case "tok" : inSurfaceForm = true; break;
            case "pos" : inPos = true; break;
            case "lemma" : inLemma = true; break;
            case "senseid" : inSenseID = true; break;
            }
            break;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (localName) {
        case "taggedtokens" :
            currentText.addSentence(currentSentence);
            addText(currentText);
            break;
        case "tagtoken":
            Word w = new WordImpl("", currentLemma, currentSurfaceForm.trim(), currentPos);
            if (!currentSenseID.equals("")) {
                String[] tokens = currentSenseID.split("\\.");
                if (tokens.length == 3) {
                    String semanticTag = getSemanticTag(tokens[0], tokens[1], Integer.valueOf(tokens[2]));
                    w.setSemanticTag(semanticTag);
                }
            }
            w.setEnclosingSentence(currentSentence);
            currentSentence.addWord(w);
            currentLemma = "";
            currentPos = "";
            currentSurfaceForm = "";
            currentSenseID = "";
            break;
        case "tag":
            inSurfaceForm = false; 
            inPos = false; 
            inLemma = false; 
            inSenseID = false;
            break;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        String appendix = "";
        for (int i = start; i < start + length; i++) appendix += ch[i];
        if (inSurfaceForm) currentSurfaceForm += appendix;
        else if (inPos) currentPos += appendix;
        else if (inLemma) currentLemma += appendix;
        else if (inSenseID) currentSenseID += appendix;
    }
    
    private String getSemanticTag(String lemma, String pos, int senseNumber)
    {   
        if (senseNumber <= 0) return "";
        IIndexWord iw = wordnet.getIndexWord(lemma, getWordnetPOS(pos));
        if (iw == null) return "";
        if (iw.getWordIDs().size() <= senseNumber - 1) return "";
        IWordID wordID = iw.getWordIDs().get(senseNumber - 1);
        IWord word = wordnet.getWord(wordID);
        String senseKey = word.getSenseKey().toString();
        return senseKey.substring(senseKey.indexOf("%") + 1);
    }
    
    private POS getWordnetPOS(String pos)
    {
        if (pos.equals("v")) return POS.VERB;
        if (pos.equals("n")) return POS.NOUN; 
        if (pos.equals("a")) return POS.ADJECTIVE;
        if (pos.equals("s")) return POS.ADJECTIVE;
        if (pos.equals("r")) return POS.ADVERB;
        return null;
    }
    
    private void openWordnet()
    {
        try {
            if (!wordnet.isOpen()) {
                wordnet.open();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
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
