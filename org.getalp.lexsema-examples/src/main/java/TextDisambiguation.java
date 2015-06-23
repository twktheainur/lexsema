import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.io.document.RawTextLoader;
import org.getalp.lexsema.io.document.TextLoader;
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
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.experiments.cuckoo.wsd.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorerWithCache;
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

        SimilarityMeasure similarityMeasure =
                new TverskiIndexSimilarityMeasureBuilder()
                        .distance(new ScaledLevenstein()).alpha(1d).beta(0.0d).gamma(0.0d).fuzzyMatching(true)
                        .build();

        ConfigurationScorer configurationScorer =
                new ConfigurationScorerWithCache(similarityMeasure);


        Disambiguator disambiguator = new CuckooSearchDisambiguator(1000,.5,1,0,configurationScorer,true);

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
        logger.error("Usage -- TextDisambiguation \"My text\"");
        System.exit(1);
    }
}
