package org.getalp.lexsema.examples;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Translation;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.dbnary.relations.DBNaryRelationType;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaMemoryStore;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


public final class DBNaryAccessQueries {
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(DBNaryAccessQueries.class);
    public static final String ONTOLOGY_PROPERTIES = "data" + File.separator + "ontology.properties";

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        Store store = new JenaTDBStore("/Users/tchechem/wsgetalp/sparqlpump/bgtest");
        StoreHandler.registerStoreInstance(store);
        StoreHandler.DEBUG_ON = true;

        OntologyModel model = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        DBNary dbnary = (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, model, new Language[] {Language.BULGARIAN});

        Vocable v = null;
        try {
            v = dbnary.getVocable(args[0]);
            logger.info(v.toString());
            List<LexicalEntry> entries = dbnary.getLexicalEntries(v);
            for (LexicalEntry le : entries) {
                List<LexicalResourceEntity> related = dbnary.getRelatedEntities(le, DBNaryRelationType.synonym);
                logger.info("\t{}", le.toString());
                logger.info("\tRelated entities:");
                for (LexicalResourceEntity lent : related) {
                    logger.info("\t\t{}", lent.toString());
                }
                logger.info("\tSenses:");
                for(LexicalSense sense: dbnary.getLexicalSenses(le)){
                    logger.info("\t\t{}", sense.toString());
                }
                logger.info("\tTranslations:");
                List<Translation> translations = dbnary.getTranslations(le);
                for (Translation translation : translations) {
                    logger.info("\t\t{}", translation.toString());
                }
            }
        } catch (NoSuchVocableException e) {
            e.printStackTrace();
        }
    }
}


