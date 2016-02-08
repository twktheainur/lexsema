package org.getalp.lexsema.io.resource.dictionary;


import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.SenseImpl;
import org.getalp.lexsema.similarity.signatures.*;
import org.getalp.lexsema.similarity.signatures.enrichment.SignatureEnrichment;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndex;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndexImpl;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class DictionaryParser implements ContentHandler {

    Map<String, List<Sense>> dico;
    String word = null;

    List<Sense> mws = null;
    Sense mw = null;
    boolean emptyDef;
    boolean ids, def;
    boolean indexed;
    @SuppressWarnings("unused")
    private Locator locator;
    private String currentSemanticSignature = "";
    private final SymbolIndex symbolIndex = new SymbolIndexImpl();


    public DictionaryParser(Map<String, List<Sense>> senseMap, boolean indexed) throws FileNotFoundException {
        super();
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        dico = senseMap;
        ids = false;
        def = false;
        locator = new LocatorImpl();
        emptyDef = false;
        this.indexed = indexed;
    }


    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
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
            case "word":
                word = atts.getValue("tag");
                mws = new ArrayList<>();
                break;
            case "ids":
                ids = true;
                break;
            case "def":
                def = true;
                currentSemanticSignature = "";
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (localName) {
            case "word":
                dico.put(word, mws);
                break;
            case "sense":
                if (mw.getSemanticSignature() != null && !isSignatureEmpty(mw.getSemanticSignature())) {
                    mws.add(mw);
                }
                break;
            case "ids":
                ids = false;
                break;
            case "def":
                def = false;
                if (indexed) {
                    IndexedSemanticSignature semanticSignature = new IndexedSemanticSignatureImpl(symbolIndex);
                    StringTokenizer st = new StringTokenizer(currentSemanticSignature);
                    while (st.hasMoreTokens()) {
                        if(indexed){
                            semanticSignature.addIndexedSymbol(Integer.valueOf(st.nextToken()));
                        }
                    }
                    mw.setSemanticSignature(semanticSignature);
                } else {
                    SemanticSignature semanticSignature = new SemanticSignatureImpl();
                    StringTokenizer st = new StringTokenizer(currentSemanticSignature);
                    while (st.hasMoreTokens()) {
                        semanticSignature.addSymbol(st.nextToken());
                    }
                    mw.setSemanticSignature(semanticSignature);
                }

                break;
        }
    }

    private boolean isSignatureEmpty(SemanticSignature semanticSignature) {
        return semanticSignature.size() == 0;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (ids) {
            StringTokenizer st = new StringTokenizer(new String(ch, start, length));
            if (st.hasMoreElements()) {
                mw = new SenseImpl(st.nextToken());
            } else {
                mw = new SenseImpl(new String(ch, start, length).trim());
            }
        } else if (def) {
            String defs = new String(ch, start, length);
            currentSemanticSignature += defs;
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
}
