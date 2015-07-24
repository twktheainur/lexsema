package org.getalp.lexsema.examples;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import com.trickl.cluster.KMeans;
import org.getalp.lexsema.acceptali.cli.org.getalp.lexsema.acceptali.acceptions.*;
import org.getalp.lexsema.acceptali.closure.similarity.PairwiseSimilarityMatrixGenerator;
import org.getalp.lexsema.acceptali.closure.similarity.PairwiseSimilarityMatrixGeneratorSim;
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
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.SenseImpl;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.crosslingual.TranslatorCrossLingualSimilarity;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.translation.GoogleWebTranslator;
import org.getalp.lexsema.translation.Translator;
import org.getalp.lexsema.util.Language;
import org.getalp.ml.matrix.filters.Filter;
import org.getalp.ml.matrix.filters.NMFKLMatrixFactorizationFilter;
import org.getalp.ml.matrix.filters.normalization.ZSignificanceNormalizationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class SenseClustering {

    public static final String ONTOLOGY_PROPERTIES = "data" + File.separator + "ontology.properties";
    private static Language[] languages = {Language.ENGLISH, Language.BULGARIAN, Language.CATALAN, Language.BULGARIAN,
    Language.FINNISH, Language.GERMAN, Language.ITALIAN, Language.JAPANESE, Language.PORTUGUESE, Language.RUSSIAN,
            Language.TURKISH, Language.FRENCH};

    private static Logger logger = LoggerFactory.getLogger(SenseClustering.class);

    public static void main(String args[]) throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchVocableException {


        Store store = new JenaTDBStore(args[0]);
        StoreHandler.registerStoreInstance(store);

        OntologyModel model = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        DBNary dbnary = (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, model, languages);


        Translator translator = new GoogleWebTranslator();
        SimilarityMeasure similarityMeasure = new TverskiIndexSimilarityMeasureBuilder()
                .fuzzyMatching(true).alpha(1d).beta(0d).gamma(0d).computeRatio(false)
                .normalize(false).regularizeOverlapInput(false).build();
        SimilarityMeasure crossLingualMeasure = new TranslatorCrossLingualSimilarity(similarityMeasure, translator);

        Vocable a = dbnary.getVocable("cat",Language.ENGLISH);
        Vocable b = dbnary.getVocable("chat",Language.FRENCH);
        Set<Sense> senses = new TreeSet<>();
        senses.addAll(vocableSenses(a,dbnary));
        senses.addAll(vocableSenses(b,dbnary));

        logger.info("Generating matrix...");
        PairwiseSimilarityMatrixGenerator matrixGenerator =
                new PairwiseSimilarityMatrixGeneratorSim(crossLingualMeasure, senses);
        matrixGenerator.generateMatrix();

        logger.info("Clustering...");
        SenseClusterer clusterer = new TricklSenseClusterer(new KMeans());
        DoubleMatrix2D inputData = matrixGenerator.getScoreMatrix();
        //inputData.normalize();

        Filter normalizationFilter = new ZSignificanceNormalizationFilter();
        Filter dimRedFilter = new NMFKLMatrixFactorizationFilter(1);

        normalizationFilter.apply(inputData);
        dimRedFilter.apply(inputData);

        List<SenseCluster> clusters = clusterer.cluster(inputData, 10, new ArrayList<>(senses));

        for (SenseCluster sc : clusters) {
            logger.info(sc.toString());
        }

        SenseClusterCombFilter filter = new SimilaritySenseClusterCombFilter(crossLingualMeasure, dimRedFilter);

        List<SenseCluster> filteredClusters = new ArrayList<>();

        for(SenseCluster senseCluster : clusters){
            filteredClusters.add(filter.apply(senseCluster));
        }

        logger.info("Filtered clusters");
        for (SenseCluster sc : clusters) {
            logger.info(sc.toString());
        }

    }

    public static Set<Sense> vocableSenses(Vocable vocable, DBNary dbNary){
        Set<Sense> localSenses = new TreeSet<>();
        for(LexicalEntry lexicalEntry: dbNary.getLexicalEntries(vocable)){
            for(LexicalSense lexicalSense: dbNary.getLexicalSenses(lexicalEntry)){
                localSenses.add(new SenseImpl(lexicalSense));
            }
        }
        return localSenses;
    }

}