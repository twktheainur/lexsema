package org.getalp.lexsema.acceptali.experiments;

import org.getalp.lexsema.acceptali.GenerateTranslationClosure;
import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class BuildingLatticeExperiment {
    public static final String DB_PATH = ".." + File.separatorChar + "data" + File.separatorChar + "dbnary" + File.separatorChar + "dbnary_full";
    public static final String ONTOLOGY_PROPERTIES = "data" + File.separatorChar + "ontology.properties";

    static Locale[] loadLanguages = {
            Language.FRENCH, Language.ENGLISH, Language.ITALIAN, Language.SPANISH,
            Language.PORTUGUESE, Language.BULGARIAN, Language.CATALAN, Language.FINNISH,
            Language.GERMAN, Language.RUSSIAN, Language.GREEK, Language.TURKISH
    };

    private BuildingLatticeExperiment() {
    }


    public static void main(String[] args) throws IOException, NoSuchVocableException {
        Store vts = new JenaTDBStore(DB_PATH);
        StoreHandler.registerStoreInstance(vts);
        StoreHandler.DEBUG_ON = true;
        OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        // Creating DBNary wrapper
        Map<Locale,DBNary> resourceMap= new HashMap<>();
        for(Locale l:loadLanguages){
            try {
                resourceMap.put(l,(DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, l));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        Vocable v = resourceMap.get(Language.ENGLISH).getVocable("river");
        List<LexicalEntry> ventries = resourceMap.get(Language.ENGLISH).getLexicalEntries(v);

        if(!ventries.isEmpty()) {
            GenerateTranslationClosure gtc = new GenerateTranslationClosure(resourceMap);
            Map<Locale, Set<LexicalResourceEntity>> closure = gtc.generateClosure(ventries.get(0),Language.ENGLISH,3);
            for(Locale l : closure.keySet()){
                System.out.println("LANGUAGE:"+l);
                for(LexicalResourceEntity lre : closure.get(l)) {
                    System.out.println("\tEntry:" + lre);
                }
            }
        }

    }
}
