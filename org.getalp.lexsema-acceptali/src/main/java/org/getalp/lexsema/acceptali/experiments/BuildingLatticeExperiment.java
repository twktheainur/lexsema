package org.getalp.lexsema.acceptali.experiments;

import org.getalp.lexsema.acceptali.LexicalResourceTranslationClosure;
import org.getalp.lexsema.acceptali.TranslationClosureGenerator;
import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
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
import java.util.List;


public final class BuildingLatticeExperiment {
    public static final String DB_PATH = File.separatorChar + "Volumes" + File.separatorChar + "RAMDisk";
    public static final String ONTOLOGY_PROPERTIES = "data" + File.separatorChar + "ontology.properties";
    static Language[] loadLanguages = {
            Language.FRENCH, Language.ENGLISH, Language.ITALIAN, Language.SPANISH,
            Language.PORTUGUESE, Language.BULGARIAN, Language.CATALAN, Language.FINNISH,
            Language.GERMAN, Language.RUSSIAN, Language.GREEK, Language.TURKISH
    };
    private static Logger logger = LoggerFactory.getLogger(BuildingLatticeExperiment.class);

    private BuildingLatticeExperiment() {
        System.setProperty("org.slf4j.simpleLogger.showShortLogName", "true");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
    }


    public static void main(String[] args) throws IOException, NoSuchVocableException {
        Store vts = new JenaTDBStore(DB_PATH);
        StoreHandler.registerStoreInstance(vts);
        //StoreHandler.DEBUG_ON = true;
        OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        // Creating DBNary wrapper

        try {
            DBNary dbNary = (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, loadLanguages);


            Vocable v = dbNary.getVocable("river", Language.ENGLISH);
            List<LexicalEntry> ventries = dbNary.getLexicalEntries(v);

            if (!ventries.isEmpty()) {
                TranslationClosureGenerator gtc = TranslationClosureGenerator.createTranslationClosureGenerator(dbNary);
                LexicalResourceTranslationClosure closure = gtc.recurseClosure(ventries.get(0), 0);
                logger.info(closure.toString());

            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
