package org.getalp.lexsema.examples;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.io.document.RawTextLoader;
import org.getalp.lexsema.io.document.TextLoader;
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
import org.getalp.lexsema.similarity.measures.word2vec.Word2VecGlossCosineSimilarity;
import org.getalp.lexsema.similarity.measures.word2vec.Word2VecGlossDistanceSimilarity;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.SimulatedAnnealing;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;
import org.getalp.ml.matrix.distance.MahalanobisDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;



public class TextDisambiguation {
    public static final String ONTOLOGY_PROPERTIES = "data" + File.separator + "ontology.properties";
    private static Logger logger = LoggerFactory.getLogger(TextSimilarity.class);

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        if(args.length<1){
            usage();
        }
        TextLoader textLoader = new RawTextLoader(new StringReader(args[0]), new EnglishDKPTextProcessor());
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
//                new Word2VecGlossDistanceSimilarity(word2VecLoader.getWord2Vec(Language.ENGLISH),
//                        new MahalanobisDistance());

        SimilarityMeasure similarityMeasure =
                new TverskiIndexSimilarityMeasureBuilder()
                        .distance(new ScaledLevenstein()).alpha(1d).beta(0.0d).gamma(0.0d).fuzzyMatching(true)
                        .build();
        ConfigurationScorer configurationScorer =
                new TverskyConfigurationScorer(similarityMeasure,Runtime.getRuntime().availableProcessors());
        Disambiguator disambiguator = new SimulatedAnnealing(0.5,0.99,5,10,configurationScorer);
        System.err.println("Loading texts");
        textLoader.load();
        for (Document document : textLoader) {
            System.err.println("\tLoading senses...");
            lrloader.loadSenses(document);
            System.err.println("\tDisambiguating... ");
            Configuration result = disambiguator.disambiguate(document);
            for(int i=0;i<result.size();i++){
                Word word = document.getWord(i);
                if(!document.getSenses(i).isEmpty()) {
                    String sense = document.getSenses(i).get(result.getAssignment(i)).getId();
                    logger.info("Sense " + sense + " assigned to " + word);
                }
            }

            System.err.println("done!");
        }
        disambiguator.release();
    }

    private static void usage() {
        logger.error("Usage -- org.getalp.lexsema.examples.TextDisambiguation \"My text\"");
        System.exit(1);
    }
}
