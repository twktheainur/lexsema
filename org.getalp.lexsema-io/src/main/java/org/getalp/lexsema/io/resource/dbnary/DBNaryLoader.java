package org.getalp.lexsema.io.resource.dbnary;

import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.SenseImpl;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.cache.SenseCache;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DBNaryLoader implements LRLoader {
    private static Logger logger = LoggerFactory.getLogger(DBNaryLoader.class);
    private final DBNary dbnary;
    private boolean hasExtendedSignature;
    private boolean shuffle;


    public DBNaryLoader(final String dbPath, final String ontologyPropertiesPath, final Locale language)
            throws IOException, InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {

        Store vts = new JenaTDBStore(dbPath);
        StoreHandler.registerStoreInstance(vts);

        OntologyModel tBox = new OWLTBoxModel(ontologyPropertiesPath);
        // Creating DBNary wrapper
        dbnary = (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, language);
    }

    private List<Sense> getSenses(String lemma, String pos) {
        String lexvoPos = posToLexvo(pos);
        Vocable v;
        List<LexicalEntry> les = null;
        List<Sense> senses = new ArrayList<>();
        try {
            v = dbnary.getVocable(lemma);
            les = dbnary.getLexicalEntries(v);
            int currentEntry = 0;
            int size = les.size();
            while (currentEntry < size) {
                LexicalEntry le = les.get(currentEntry);
                if (pos != null && !lexvoPos.equals(le.getPartOfSpeech())) {
                    les.remove(currentEntry);
                    size--;
                } else {
                    for (LexicalSense ls : dbnary.getLexicalSenses(le)) {
                        Sense s = new SenseImpl(ls);
                        SemanticSignature signature = new SemanticSignatureImpl();
                        String def = ls.getDefinition();
                        addToSignature(signature, def);
                        s.setSemanticSignature(signature);
                        senses.add(s);
                    }
                    currentEntry++;
                }
            }
        } catch (NoSuchVocableException e) {
            logger.error(e.toString());
        }
        return senses;
    }

    @Override
    public List<Sense> getSenses(Word w) {
        List<Sense> senses;
        senses = SenseCache.getInstance().getSenses(w);
        if (senses == null) {
            if (w != null) {
                senses = getSenses(w.getLemma(), w.getPartOfSpeech());
            }
            if (shuffle && senses != null) {
                Collections.shuffle(senses);
            }
            SenseCache.getInstance().addToCache(w, senses);
        }
        return senses;
    }

    private void addToSignature(SemanticSignature signature, String def) {
        StringTokenizer st = new StringTokenizer(def, " ", false);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            signature.addSymbol(token, 1.0);
        }
    }

    private String posToLexvo(final String pos) {
        String convertedPos = "";
        if (pos.contains("lexvo")) {
            convertedPos = pos;
        } else if (pos.toLowerCase().startsWith("n")
                || pos.toLowerCase().contains("noun")) {
            convertedPos = "lexvo:noun";
        } else if (pos.toLowerCase().startsWith("v")
                || pos.toLowerCase().contains("verb")) {
            convertedPos = "lexvo:verb";
        } else if (pos.toLowerCase().startsWith("j") ||
                pos.toLowerCase().contains("adj") ||
                pos.toLowerCase().contains("adjective") ||
                pos.toLowerCase().contains("s")) {
            convertedPos = "lexvo:adjective";
        } else if (pos.toLowerCase().startsWith("r") ||
                pos.toLowerCase().contains("adv") ||
                pos.toLowerCase().contains("adverb")) {
            convertedPos = "lexvo:adverb";
        }
        return convertedPos;
    }

    @Override
    public List<List<Sense>> getAllSenses(List<Word> wds) {
        List<List<Sense>> senses = new ArrayList<>();
        for (Word w : wds) {
            senses.add(getSenses(w));
        }
        return senses;
    }

    @Override
    public void loadSenses(Document document) {
        for (Word w : document) {
            document.addWordSenses(getSenses(w));
        }
    }

    public DBNaryLoader setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
        return this;
    }
}
