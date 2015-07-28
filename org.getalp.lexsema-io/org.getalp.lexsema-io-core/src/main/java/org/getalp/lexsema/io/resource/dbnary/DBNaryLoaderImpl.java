package org.getalp.lexsema.io.resource.dbnary;


import com.hp.hpl.jena.graph.Node;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurus;
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
    private static final Logger logger = LoggerFactory.getLogger(DBNaryLoaderImpl.class);
    private final DBNary dbnary;
    private final OntologyModel model;
    SenseCache senseCache;
    private boolean shuffle;
    private boolean loadDefinitions = true;
    private final Language language;


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
        shuffle = false;
    }

    public DBNaryLoaderImpl(DBNary dbNary)
            throws IOException, InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {
        model = dbNary.getModel();
        // Creating DBNary wrapper
        dbnary = dbNary;
        senseCache = SenseCacheImpl.getInstance();
        shuffle = false;
        language = dbNary.getLanguage();
    }

    public DBNaryLoaderImpl(DBNary dbNary, Language language)
            throws IOException, InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException, InstantiationException, IllegalAccessException {
        model = dbNary.getModel();
        // Creating DBNary wrapper
        dbnary = dbNary;
        senseCache = SenseCacheImpl.getInstance();
        this.language = language;
        shuffle = false;
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
                    //noinspection BreakStatement
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
                Sense sense = new SenseImpl(ls);
                SemanticSignature signature = new SemanticSignatureImpl();
                if (loadDefinitions) {
                    String def = ls.getDefinition();
                    addToSignature(signature, def);
                }
                sense.setSemanticSignature(signature);
                senses.add(sense);
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

    private void addToSignature(SemanticSignature signature, String def) {
        StringTokenizer st = new StringTokenizer(def, " ", false);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            signature.addSymbol(token, 1.0);
        }
    }

    @SuppressWarnings("OverlyComplexMethod")
    private String posToLexvo(final String pos) {
        String convertedPos = "";
        String lpos = pos.toLowerCase();
        //noinspection IfStatementWithTooManyBranches
        if ((lpos.startsWith("n")
                || lpos.contains("noun")) && language == Language.ENGLISH) {
            convertedPos = "lexinfo:noun";
        } else if (lpos.startsWith("v")
                || lpos.contains("verb")) {
            convertedPos = "lexinfo:verb";
        } else if (pos.toLowerCase().startsWith("j") ||
                lpos.contains("adj") ||
                lpos.contains("adjective") ||
                lpos.contains("s") ||
                lpos.contains("a")) {
            convertedPos = "lexinfo:adjective";
        } else if (pos.toLowerCase().startsWith("r") ||
                lpos.contains("adv") ||
                lpos.contains("adverb")) {
            convertedPos = "lexinfo:adverb";
        } else if(lpos.contains("adjf")){
            convertedPos = "прил";
        } else if ((lpos.startsWith("n")
                || lpos.contains("noun")) && language == Language.RUSSIAN){
            convertedPos = "сущ";
        } else if(lpos.startsWith("pr") && language == Language.RUSSIAN){
            convertedPos = "гл";
        }
        Node node = model.getNode(convertedPos);
        return node.toString();
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
    public LRLoader loadRelated(boolean loadRelated) {
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader stemming(boolean stemming) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader filterStopWords(boolean usesStopWords) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LRLoader addThesaurus(AnnotatedTextThesaurus thesaurus) {
        return this;
    }

    @Override
    public LRLoader index(boolean useIndex) {
        return this;
    }

}
