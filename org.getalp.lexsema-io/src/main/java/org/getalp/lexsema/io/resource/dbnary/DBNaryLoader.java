package org.getalp.lexsema.io.resource.dbnary;


import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.language.Language;
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
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("OverlyCoupledClass")
public class DBNaryLoader implements LRLoader {
    private static Logger logger = LoggerFactory.getLogger(DBNaryLoader.class);
    private final DBNary dbnary;
    private final OntologyModel model;
    private boolean shuffle;
    SenseCache senseCache;
    private boolean loadDefinitions;
    private boolean loadRelated;


    public DBNaryLoader(final String dbPath, final String ontologyPropertiesPath, final Language language)
            throws IOException, InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {

        Store vts = new JenaTDBStore(dbPath);
        StoreHandler.registerStoreInstance(vts);
        model = new OWLTBoxModel(ontologyPropertiesPath);
        // Creating DBNary wrapper
        dbnary = (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, model, language);
        senseCache = SenseCacheImpl.getInstance();
    }

    private List<Sense> getLexicalEntriesAndSenses(Word w) {
        String lemma = w.getLemma();
        String pos = w.getPartOfSpeech();
        String lexvoPos = posToLexvo(pos);
        Vocable v;
        List<LexicalEntry> les;
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
                    w.setLexicalEntry(le);
                    for (LexicalSense ls : dbnary.getLexicalSenses(le)) {
                        Sense s = new SenseImpl(ls);
                        SemanticSignature signature = new SemanticSignatureImpl();
                        if(loadDefinitions) {
                            String def = ls.getDefinition();
                            addToSignature(signature, def);
                        }
                        s.setSemanticSignature(signature);
                        senses.add(s);
                    }
                    break;
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

    private void addToSignature(SemanticSignature signature, String def) {
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
    public LRLoader suffle(boolean shuffle) {
        this.shuffle = shuffle;
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader extendedSignature(boolean hasExtendedSignature) {
        return this;
    }

    @Override
    public LRLoader loadDefinition(boolean loadDefinitions) {
        this.loadDefinitions = loadDefinitions;
        return this;
    }

    @Override
    public LRLoader setLoadRelated(boolean loadRelated) {
        this.loadRelated = loadRelated;
        return this;
    }

	@Override
	public LRLoader setStemming(boolean stemming) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LRLoader setUsesStopWords(boolean usesStopWords) {
		// TODO Auto-generated method stub
		return null;
	}
}
