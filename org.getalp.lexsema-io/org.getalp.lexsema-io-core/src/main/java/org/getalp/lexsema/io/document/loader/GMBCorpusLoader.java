package org.getalp.lexsema.io.document.loader;


import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import org.getalp.lexsema.similarity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

public class GMBCorpusLoader extends CorpusLoaderImpl implements ContentHandler {

    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.DEFAULT;

    private static final Logger logger = LoggerFactory.getLogger(GMBCorpusLoader.class);

    private final String path;

    private final Dictionary wordnet;

    @SuppressWarnings("all")
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
        inPos =false;
        inLemma = false;
        inSenseID = false;
        inSurfaceForm = false;
        currentSurfaceForm="";
        currentPos = "";
        currentLemma = "";
        currentSenseID = "";
        currentSentence = DOCUMENT_FACTORY.nullSentence();
        currentText = DOCUMENT_FACTORY.nullText();
    }

    @Override
    public void load() {
        try {
            saxReader = XMLReaderFactory.createXMLReader();
            saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            saxReader.setContentHandler(this);
        } catch (SAXException e) {
            logger.error(MessageFormat.format("[GMB] Error while creating corpus streaming XML parser: {0}", e.getLocalizedMessage()));
        }
        openWordnet();
        File data = new File(path + "/data/");
        for (String childName : data.list()) {
            File child = new File(String.format("%s/data/%s", path, childName));
            if(child.isDirectory()) {
                processDirectory(child);
            }
        }
    }

    @Override
    public CorpusLoader loadNonInstances(boolean loadExtra) {
        return this;
    }

    private void processDirectory(File dir) {
        logger.info(MessageFormat.format("[GMB] Processing directory {0} ...", dir.getName()));
        String[] directories = dir.list();
        for (String child : directories) {
            String fileName = String.format("%s%s%s", dir.getAbsolutePath(), File.separator, child);
            if(!child.startsWith(".")) {
                processFile(fileName);
            }
        }
    }

    private void processFile(String filePath) {
        //logger.error("[GMB] Processing file " + filePath + "...");
        try {
            saxReader.parse(String.format("%s%sen.drs.xml", filePath, File.separator));
        } catch (SAXException e) {
            logger.error(MessageFormat.format("[GMB] An error occurred during the parsing of a corpus file:{0}", e.getLocalizedMessage()));
        } catch (IOException e) {
            logger.error(MessageFormat.format("[GMB] An error occurred while reading a corpus file:{0}", e.getLocalizedMessage()));
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        switch (localName) {
            case "taggedtokens":
                currentText = DOCUMENT_FACTORY.createText();
                currentSentence = DOCUMENT_FACTORY.createSentence("");
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
            case "tag":
                String type = atts.getValue("type");
                processTagType(type);
                break;
        }
    }

    private void processTagType(String type){
        switch (type) {
            case "tok":
                inSurfaceForm = true;
                break;
            case "pos":
                inPos = true;
                break;
            case "lemma":
                inLemma = true;
                break;
            case "senseid":
                inSenseID = true;
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (localName) {
            case "taggedtokens":
                addText(currentText);
                break;
            case "tagtoken":
                Word w = DOCUMENT_FACTORY.createWord("", currentLemma, currentSurfaceForm.trim(), currentPos);
                if (!currentSenseID.isEmpty()) {
                    String[] tokens = currentSenseID.split("\\.");
                    if (tokens.length == 3) {
                        String semanticTag = getSemanticTag(tokens[0], tokens[1], Integer.valueOf(tokens[2]));
                        w.setSemanticTag(semanticTag);
                    }
                }
                w.setEnclosingSentence(currentSentence);
                currentSentence.addWord(w);
                if (currentLemma.equals(".")) {
                    currentText.addSentence(currentSentence);
                    currentSentence = DOCUMENT_FACTORY.createSentence("");
                }
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

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        StringBuilder appendix = new StringBuilder();
        for (int i = start; i < start + length; i++) {
            appendix.append(ch[i]);
        }
        if (inSurfaceForm) {
            currentSurfaceForm += appendix.toString();
        } else if (inPos) {
            currentPos += appendix.toString();
        } else if (inLemma) {
            currentLemma += appendix.toString();
        } else if (inSenseID) {
            currentSenseID += appendix.toString();
        }
    }

    private String getSemanticTag(String lemma, CharSequence pos, int senseNumber) {
        String semanticTag = "";
        if (senseNumber > 0) {
            @SuppressWarnings("ConstantConditions") IIndexWord iw = wordnet.getIndexWord(lemma, getWordnetPOS(pos));
            if (iw != null){
                List<IWordID> wordIDs = iw.getWordIDs();
                if(wordIDs.size() > senseNumber - 1){
                    IWordID wordID = wordIDs.get(senseNumber - 1);
                    IWord word = wordnet.getWord(wordID);
                    String senseKey = word.getSenseKey().toString();
                    semanticTag = senseKey.substring(senseKey.indexOf("%") + 1);
                }
            }
        }
        return semanticTag;
    }

    private POS getWordnetPOS(CharSequence pos) {
        return POS.getPartOfSpeech(pos.charAt(0));
    }

    private void openWordnet() {
        try {
            if (!wordnet.isOpen()) {
                wordnet.open();
            }
        } catch (IOException e) {
            logger.error(MessageFormat.format("[GMB] Error while opening WordNet: {0}", e.getLocalizedMessage()));
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
    public void processingInstruction(String target, String data) throws SAXException {

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
