package org.getalp.lexsema.examples;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.RawCorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dbnary.DBNaryLoaderImpl;
import org.getalp.lexsema.io.text.EnglishDKPTextProcessor;
import org.getalp.lexsema.io.word2vec.MultilingualSerializedModelWord2VecLoader;
import org.getalp.lexsema.io.word2vec.MultilingualWord2VecLoader;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaRemoteSPARQLStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.SimulatedAnnealing;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;



public class TextDisambiguation {
    public static final String ONTOLOGY_PROPERTIES = "data" + File.separator + "ontology.properties";
    private static final Logger logger = LoggerFactory.getLogger(TextSimilarity.class);
    private static final double SA_P_0 = 0.5;
    private static final double SA_COOLING_RATE = 0.99;
    private static final int CONVERGENCE_THRESHOLD = 5;
    private static final int ITERATIONS = 10;

    private TextDisambiguation() {
    }

    public static void main(String... args) throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        if(args.length<1){
            usage();
        }
        CorpusLoader corpusLoader = new RawCorpusLoader(new StringReader(args[0]), new EnglishDKPTextProcessor());
        Store store = new JenaRemoteSPARQLStore("http://kaiko.getalp.org/sparql");
        StoreHandler.registerStoreInstance(store);
        OntologyModel model = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        DBNary dbnary = (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, model, new Language[] {Language.ENGLISH});
        LRLoader lrloader = new DBNaryLoaderImpl(dbnary, Language.ENGLISH).loadDefinitions(true);
        /*LRLoader lrloader = new WordnetLoader2("../data/wordnet/2.1/dict")
                .extendedSignature(true).loadDefinitions(true);*/
        MultilingualWord2VecLoader word2VecLoader = new MultilingualSerializedModelWord2VecLoader();
        word2VecLoader.loadGoogle(new File(args[1]),true);

//        SimilarityMeasure similarityMeasure =
//                new Word2VecGlossDistanceSimilarity(word2VecLoader.getWordVectors(Language.ENGLISH),
//                        new MahalanobisDistance());

        @SuppressWarnings("LawOfDemeter") SimilarityMeasure similarityMeasure =
                new TverskiIndexSimilarityMeasureBuilder()
                        .distance(new ScaledLevenstein()).alpha(1d).beta(0.0d).gamma(0.0d).fuzzyMatching(true)
                        .build();
        ConfigurationScorer configurationScorer =
                new TverskyConfigurationScorer(similarityMeasure,Runtime.getRuntime().availableProcessors());
        Disambiguator disambiguator = new SimulatedAnnealing(SA_P_0, SA_COOLING_RATE,CONVERGENCE_THRESHOLD,ITERATIONS,configurationScorer);
        logger.info("Loading texts");
        corpusLoader.load();
        for (Document document : corpusLoader) {
            logger.info("\tLoading senses...");
            lrloader.loadSenses(document);
            logger.info("\tDisambiguating... ");
            Configuration result = disambiguator.disambiguate(document);
            for(int i=0;i<result.size();i++){
                Word word = document.getWord(i);
                if(!document.getSenses(i).isEmpty()) {
                    String sense = document.getSenses(i).get(result.getAssignment(i)).getId();
                    logger.info("Sense {} assigned to {}", sense, word);
                }
            }

            logger.info("done!");
        }
        disambiguator.release();
    }

    private static void usage() {
        logger.error("Usage -- org.getalp.lexsema.examples.TextDisambiguation \"My text\"");
        System.exit(1);
    }
}
