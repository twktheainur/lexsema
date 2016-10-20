package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.DefaultDocumentFactory;
import org.getalp.lexsema.similarity.DocumentFactory;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordnetGlossTagEnrichment extends SignatureEnrichmentAbstract implements ContentHandler
{
    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.DEFAULT_DOCUMENT_FACTORY;
    private static final Logger logger = LoggerFactory.getLogger(WordnetGlossTagEnrichment.class);

    private final Map<String, Sentence> synset;
    
    private final Map<String, String> senseKeyToSynsetID;
    
    private final Map<String, List<String>> lemmaPOSToSenseKeys;

    private final String path;

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
        } catch (SAXException e) {
            logger.error(e.getLocalizedMessage());
        }
        isLoaded = false;
    }

    public void load() {
        if (!isLoaded) {
            processFile(MessageFormat.format("{0}/merged/noun.xml", path), "n");
            processFile(MessageFormat.format("{0}/merged/adj.xml", path), "a");
            processFile(MessageFormat.format("{0}/merged/verb.xml", path), "v");
            processFile(MessageFormat.format("{0}/merged/adv.xml", path), "r");
            isLoaded = true;
        }
    }

    private void processFile(String filePath, String globalPOS) {
        logger.debug("[WordnetGlossTag] Processing file {}...", filePath);
        currentGlobalPOS = globalPOS;
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
            currentSentence = DOCUMENT_FACTORY.createSentence("");
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
            if (currentLemma != null && currentLemma.contains("%")) {
                currentLemma = currentLemma.substring(0, currentLemma.indexOf("%"));
            }
            break;
        case "id":
            if (inWord && currentSemanticTag.isEmpty()) {
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
            break;
        case "synset":
            synset.put(currentSynsetID, currentSentence);
            break;
        case "sk":
            senseKeyToSynsetID.put(currentSenseKey, currentSynsetID);
            String zeLemma = currentSenseKey.substring(0, currentSenseKey.indexOf('%'));
            String lemmaPOS = String.format("%s%%%s", zeLemma, currentGlobalPOS);
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
            Word w = DOCUMENT_FACTORY.createWord("", currentLemma, currentSurfaceForm.trim(), currentPos);
            w.setSemanticTag(currentSemanticTag);
            if (!tagIgnore) {
                w.setEnclosingSentence(currentSentence);
            }
            if (!tagIgnore) {
                currentSentence.addWord(w);
            }
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
        } else if (inSenseKey) {
            for (int i = start; i < start + length; i++) {
                currentSenseKey += ch[i];
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
    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature)
    {
        return semanticSignature;
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, String id)
    {
        load();
        SemanticSignature newSemanticSignature = DefaultSemanticSignatureFactory.DEFAULT.createSemanticSignature();
        for (String word : semanticSignature.getStringSymbols()) {
            newSemanticSignature.addSymbol(word);
        }
        String synsetID = senseKeyToSynsetID.get(id);
        if (synsetID == null && id.contains("%5")) synsetID = senseKeyToSynsetID.get(id.replace("%5", "%3"));
        if (synsetID == null && id.contains("%3")) synsetID = senseKeyToSynsetID.get(id.replace("%3", "%5"));
        if (synsetID == null)
        {
            logger.error("Warning : sense key not found : {}", id);
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
