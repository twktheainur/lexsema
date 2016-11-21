package org.getalp.lexsema.io.resource.dictionary;


import org.getalp.lexsema.similarity.DefaultDocumentFactory;
import org.getalp.lexsema.similarity.DocumentFactory;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.signatures.*;
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

    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.DEFAULT;

    private final Map<String, List<Sense>> dictionary;
    private String word;

    private List<Sense> mws;
    private Sense mw;
    private boolean ids, def;
    private final boolean indexed;
    private final boolean vectorized;
    @SuppressWarnings("unused")
    private Locator locator;
    private String currentSemanticSignature = "";
    private final SymbolIndex symbolIndex = new SymbolIndexImpl();
    private String currentId = "";

    public DictionaryParser(Map<String, List<Sense>> senseMap, boolean indexed, boolean vectorized) throws FileNotFoundException {
        super();
        dictionary = senseMap;
        ids = false;
        def = false;
        locator = new LocatorImpl();
        this.indexed = indexed;
        this.vectorized = vectorized;
    }
    
    public DictionaryParser(Map<String, List<Sense>> senseMap, boolean indexed) throws FileNotFoundException {
        this(senseMap, indexed, false);
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
                currentId = "";
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
                dictionary.put(word, mws);
                break;
            case "sense":
                if (mw.getSemanticSignature() != null) {
                    mws.add(mw);
                }
                break;
            case "ids":
                ids = false;
                mw = DOCUMENT_FACTORY.createSense(currentId.trim());
                break;
            case "def":
                def = false;
                if (indexed) {
                    IndexedSemanticSignature semanticSignature = DefaultSemanticSignatureFactory.DEFAULT.createIndexedSemanticSignature(symbolIndex);
                    StringTokenizer st = new StringTokenizer(currentSemanticSignature);
                    while (st.hasMoreTokens()) {
                        semanticSignature.addIndexedSymbol(Integer.valueOf(st.nextToken()));
                    }
                    mw.setSemanticSignature(semanticSignature);
                } else if (vectorized) {
                    VectorizedSemanticSignature semanticSignature = DefaultSemanticSignatureFactory.DEFAULT.createVectorizedSemanticSignature();
                    StringTokenizer st = new StringTokenizer(currentSemanticSignature);
                    while (st.hasMoreTokens()) {
                        semanticSignature.addSymbol(st.nextToken());
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
            currentId += new String(ch, start, length);
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
