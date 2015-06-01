package org.getalp.lexsema.io.resource.dbnary;


import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
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
import org.getalp.lexsema.similarity.cache.SenseCacheImpl;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignatureImpl;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

@SuppressWarnings("OverlyCoupledClass")
public class DBNaryLoaderImpl implements DBNaryLoader {
    private static Logger logger = LoggerFactory.getLogger(DBNaryLoaderImpl.class);
    private final DBNary dbnary;
    private final OntologyModel model;
    SenseCache senseCache;
    private boolean shuffle;
    private boolean loadDefinitions;
    private boolean loadRelated;
    private Language language;


    public DBNaryLoaderImpl(final String dbPath, final String ontologyPropertiesPath, final Language language)
            throws IOException, InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {

        Store vts = new JenaTDBStore(dbPath);
        StoreHandler.registerStoreInstance(vts);
        model = new OWLTBoxModel(ontologyPropertiesPath);
        // Creating DBNary wrapper
        dbnary = (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, model, language);
        senseCache = SenseCacheImpl.getInstance();
        this.language = language;
    }

    public DBNaryLoaderImpl(DBNary dbNary)
            throws IOException, InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {
        model = dbNary.getModel();
        // Creating DBNary wrapper
        dbnary = dbNary;
        senseCache = SenseCacheImpl.getInstance();
    }

    public DBNaryLoaderImpl(DBNary dbNary, Language language)
            throws IOException, InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {
        model = dbNary.getModel();
        // Creating DBNary wrapper
        dbnary = dbNary;
        senseCache = SenseCacheImpl.getInstance();
        this.language = language;
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public LexicalEntry retrieveLexicalEntryForWord(Word targetWord) {
        String lemma = targetWord.getLemma();
        String pos = targetWord.getPartOfSpeech();
        String lexvoPos = posToLexvo(pos);
        Vocable v;
        List<LexicalEntry> les;
        LexicalEntry returnEntry = null;
        try {
            v = dbnary.getVocable(lemma, language);
            les = dbnary.getLexicalEntries(v);
            int currentEntry = 0;
            int size = les.size();
            while (currentEntry < size) {
                LexicalEntry le = les.get(currentEntry);
                if (pos != null && !lexvoPos.equals(le.getPartOfSpeech())) {
                    les.remove(currentEntry);
                    size--;
                } else {
                    returnEntry = le;
                    targetWord.setLexicalEntry(le);
                    break;
                }
            }
        } catch (NoSuchVocableException e) {
            logger.warn(e.toString());
        }
        return returnEntry;
    }

    private List<Sense> getLexicalEntriesAndSenses(Word w) {
        List<Sense> senses = new ArrayList<>();
        LexicalEntry le = retrieveLexicalEntryForWord(w);
        if (le != null) {
            for (LexicalSense ls : dbnary.getLexicalSenses(le)) {
                Sense s = new SenseImpl(ls);
                StringSemanticSignature signature = new StringSemanticSignatureImpl();
                if (loadDefinitions) {
                    String def = ls.getDefinition();
                    addToSignature(signature, def);
                }
                s.setSemanticSignature(signature);
                senses.add(s);
            }
        }
        return senses;
    }

    @Override
    public List<Sense> getSenses(Word w) {
        List<Sense> senses;
        senses = senseCache.getSenses(w);
        if (senses == null) {
            if (w != null) {
                senses = getLexicalEntriesAndSenses(w);
            }
            if (shuffle && senses != null) {
                Collections.shuffle(senses);
            }
            senseCache.addToCache(w, senses);
        }
        return senses;
    }

    private void addToSignature(StringSemanticSignature signature, String def) {
        StringTokenizer st = new StringTokenizer(def, " ", false);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            signature.addSymbol(token, 1.0);
        }
    }

    private String posToLexvo(final String pos) {
        String convertedPos = "";
        if (pos.toLowerCase().startsWith("n")
                || pos.toLowerCase().contains("noun")) {
            convertedPos = "lexinfo:noun";
        } else if (pos.toLowerCase().startsWith("v")
                || pos.toLowerCase().contains("verb")) {
            convertedPos = "lexinfo:verb";
        } else if (pos.toLowerCase().startsWith("j") ||
                pos.toLowerCase().contains("adj") ||
                pos.toLowerCase().contains("adjective") ||
                pos.toLowerCase().contains("s") ||
                pos.toLowerCase().contains("a")) {
            convertedPos = "lexinfo:adjective";
        } else if (pos.toLowerCase().startsWith("r") ||
                pos.toLowerCase().contains("adv") ||
                pos.toLowerCase().contains("adverb")) {
            convertedPos = "lexinfo:adverb";
        }
        return model.getNode(convertedPos).toString();
    }

    @Override
    public void loadSenses(Document document) {
        for (Word w : document) {
            document.addWordSenses(getSenses(w));
        }
    }


    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader shuffle(boolean shuffle) {
        this.shuffle = shuffle;
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader extendedSignature(boolean hasExtendedSignature) {
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader loadDefinitions(boolean loadDefinitions) {
        this.loadDefinitions = loadDefinitions;
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader setLoadRelated(boolean loadRelated) {
        this.loadRelated = loadRelated;
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader setStemming(boolean stemming) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader setUsesStopWords(boolean usesStopWords) {
        // TODO Auto-generated method stub
        return null;
    }
}
