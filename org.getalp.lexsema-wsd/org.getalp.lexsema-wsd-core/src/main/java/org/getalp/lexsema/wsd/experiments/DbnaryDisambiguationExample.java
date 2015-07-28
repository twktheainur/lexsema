package org.getalp.lexsema.wsd.experiments;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.RawCorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dbnary.DBNaryLoaderImpl;
import org.getalp.lexsema.io.text.EnglishDKPTextProcessor;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaRemoteSPARQLStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.SimulatedAnnealing;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("all")
public class DbnaryDisambiguationExample {

    public static final String ONTOLOGY_PROPERTIES = "data" + File.separatorChar + "ontology.properties";

    public static void main(String[] args) throws NoSuchMethodException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        CorpusLoader dl = new RawCorpusLoader(new FileReader("../data/text.txt"), new EnglishDKPTextProcessor());

        Store store = new JenaRemoteSPARQLStore("http://kaiko.getalp.org/sparql");
        StoreHandler.registerStoreInstance(store);

        OntologyModel model = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        DBNary dbnary = (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, model, Language.ENGLISH);
        LRLoader lrloader = new DBNaryLoaderImpl(dbnary, Language.ENGLISH).shuffle(true);

        SimilarityMeasure similarityMeasure =
                new TverskiIndexSimilarityMeasureBuilder()
                        .distance(new ScaledLevenstein()).alpha(1d).beta(0.0d).gamma(0.0d).fuzzyMatching(true)
                        .build();

        ConfigurationScorer configurationScorer =
                new TverskyConfigurationScorer(similarityMeasure, Runtime.getRuntime().availableProcessors());

        Disambiguator disambiguator = new SimulatedAnnealing(0.8,0.8,5,100,configurationScorer);

        System.err.println("Loading texts");
        dl.load();

        for (Document d : dl) {
            System.err.println("\tLoading senses...");
            lrloader.loadSenses(d);
            System.err.println("\tDisambiguating... ");
            Configuration c = disambiguator.disambiguate(d);
            System.err.println("done!");
        }
        disambiguator.release();
    }
}
