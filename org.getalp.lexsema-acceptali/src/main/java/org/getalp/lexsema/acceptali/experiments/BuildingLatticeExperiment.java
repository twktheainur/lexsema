package org.getalp.lexsema.acceptali.experiments;

import org.getalp.lexsema.acceptali.GenerateTranslationClosure;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public final class BuildingLatticeExperiment {
    public static final String DB_PATH = ".." + File.separatorChar + "data" + File.separatorChar + "dbnary" + File.separatorChar + "dbnary_full";
    public static final String ONTOLOGY_PROPERTIES = "data" + File.separatorChar + "ontology.properties";

    private static Logger logger = LoggerFactory.getLogger(BuildingLatticeExperiment.class);

    static Language[] loadLanguages = {
            Language.FRENCH, Language.ENGLISH, Language.ITALIAN, Language.SPANISH,
            Language.PORTUGUESE, Language.BULGARIAN, Language.CATALAN, Language.FINNISH,
            Language.GERMAN, Language.RUSSIAN, Language.GREEK, Language.TURKISH
    };

    private BuildingLatticeExperiment() {
        System.setProperty("org.slf4j.simpleLogger.showShortLogName","true");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
    }


    public static void main(String[] args) throws IOException, NoSuchVocableException {
        Store vts = new JenaTDBStore(DB_PATH);
        StoreHandler.registerStoreInstance(vts);
        //StoreHandler.DEBUG_ON = true;
        OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        // Creating DBNary wrapper
        Map<Language,DBNary> resourceMap= new HashMap<>();
        for(Language l:loadLanguages){
            try {
                resourceMap.put(l,(DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, l));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        Vocable v = resourceMap.get(Language.ENGLISH).getVocable("river");
        List<LexicalEntry> ventries = resourceMap.get(Language.ENGLISH).getLexicalEntries(v);


        if(!ventries.isEmpty()) {
            GenerateTranslationClosure gtc = new GenerateTranslationClosure(resourceMap);
            Map<Language, Map<LexicalEntry,Set<LexicalSense>>> closure = gtc.recurseClosure(ventries.get(0), Language.ENGLISH, 0);
            for(Language l : closure.keySet()){
                logger.info("LANGUAGE:" + l);
                for(LexicalEntry le: closure.get(l).keySet()) {
                    logger.info("\tLexicalEntry: " + le);
                    for (LexicalSense ls : closure.get(l).get(le)) {
                        logger.info("\t\tEntry:" + ls);
                    }
                }
            }
        }

    }
}
