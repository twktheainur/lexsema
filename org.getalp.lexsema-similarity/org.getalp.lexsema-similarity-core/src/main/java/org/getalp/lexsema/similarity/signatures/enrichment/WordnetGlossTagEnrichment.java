package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.SentenceImpl;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.WordImpl;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordnetGlossTagEnrichment extends SignatureEnrichmentAbstract implements ContentHandler
{
    private Map<String, Sentence> synset;
    
    private Map<String, String> senseKeyToSynsetID;
    
    private Map<String, List<String>> lemmaPOSToSenseKeys;

    private String path;

    private XMLReader saxReader;
    
    private String currentGlobalPOS;

    private Sentence currentSentence;

    private boolean inWord;
    
    private String currentSynsetID;

    private String currentLemma;

    private String currentPos;

    private String currentSurfaceForm;

    private String currentSemanticTag;
    
    private boolean inSenseKey;
    
    private String currentSenseKey;
    
    private boolean tagIgnore;
    
    private boolean isLoaded;

    public WordnetGlossTagEnrichment(String path) {
        this.path = path;
        synset = new HashMap<>();
        senseKeyToSynsetID = new HashMap<>();
        lemmaPOSToSenseKeys = new HashMap<>();
        try {
            saxReader = XMLReaderFactory.createXMLReader();
            saxReader.setContentHandler(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isLoaded = false;
    }

    public void load() {
        if (!isLoaded) {
            processFile(path + "/merged/noun.xml", "n");
            processFile(path + "/merged/adj.xml", "a");
            processFile(path + "/merged/verb.xml", "v");
            processFile(path + "/merged/adv.xml", "r");
            isLoaded = true;
        }
    }

    private void processFile(String filePath, String globalPOS) {
        System.out.println("[WordnetGlossTag] Processing file " + filePath + "...");
        currentGlobalPOS = globalPOS;
        try {
            saxReader.parse(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        switch (localName) {
        case "wordnet" :
            inWord = false;
            inSenseKey = false;
            currentSynsetID = "";
            currentLemma = "";
            currentPos = "";
            currentSurfaceForm = "";
            currentSemanticTag = "";
            break;
        case "synset":
            currentSynsetID = atts.getValue("id");
            currentSentence = new SentenceImpl("");
            break;
        case "sk":
            inSenseKey = true;
            currentSenseKey = "";
            break;
        case "wf":
            inWord = true;
            currentPos = atts.getValue("pos");
            currentLemma = atts.getValue("lemma");
            tagIgnore = "ignore".equals(atts.getValue("tag"));
            if (currentLemma != null && currentLemma.indexOf("%") != -1) {
                currentLemma = currentLemma.substring(0, currentLemma.indexOf("%"));
            }
            break;
        case "id":
            if (inWord && currentSemanticTag.equals("")) {
                currentSemanticTag = atts.getValue("sk");
                currentSemanticTag = currentSemanticTag.substring(currentSemanticTag.indexOf("%") + 1);
            }
            break;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (localName) {
        case "wordnet" :
            break;
        case "synset":
            synset.put(currentSynsetID, currentSentence);
            break;
        case "sk":
            senseKeyToSynsetID.put(currentSenseKey, currentSynsetID);
            String zeLemma = currentSenseKey.substring(0, currentSenseKey.indexOf('%'));
            String lemmaPOS = zeLemma + "%" + currentGlobalPOS;
            if (lemmaPOSToSenseKeys.containsKey(lemmaPOS)) {
                lemmaPOSToSenseKeys.get(lemmaPOS).add(currentSenseKey);
            } else {
                List<String> newList = new ArrayList<>();
                newList.add(currentSenseKey);
                lemmaPOSToSenseKeys.put(lemmaPOS, newList);
            }
            currentSenseKey = "";
            inSenseKey = false;
            break;
        case "wf":
            currentSurfaceForm = currentSurfaceForm.trim();
            Word w = new WordImpl("", currentLemma, currentSurfaceForm.trim(), currentPos);
            w.setSemanticTag(currentSemanticTag);
            if (!tagIgnore)
                w.setEnclosingSentence(currentSentence);
            if (!tagIgnore) 
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
        } else if (inSenseKey) {
            for (int i = start; i < start + length; i++) {
                currentSenseKey += ch[i];
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
    
    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature)
    {
        return semanticSignature;
    }

    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, String senseKey)
    {
        load();
        SemanticSignature newSemanticSignature = new SemanticSignatureImpl();
        for (String word : semanticSignature.getStringSymbols()) {
            newSemanticSignature.addSymbol(word);
        }
        String synsetID = senseKeyToSynsetID.get(senseKey);
        if (synsetID == null && senseKey.contains("%5")) synsetID = senseKeyToSynsetID.get(senseKey.replace("%5", "%3"));
        if (synsetID == null && senseKey.contains("%3")) synsetID = senseKeyToSynsetID.get(senseKey.replace("%3", "%5"));
        if (synsetID == null)
        {
            System.err.println("Warning : sense key not found : " + senseKey);
            return newSemanticSignature;
        }
        Sentence synsetSentence = synset.get(synsetID);
        for (Word word : synsetSentence) {
            String symbolToAdd = word.getSurfaceForm();
            newSemanticSignature.addSymbol(symbolToAdd);
        }
        return newSemanticSignature;
    }

}
