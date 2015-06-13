package org.getalp.lexsema.acceptali.experiments;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import com.trickl.cluster.KMeans;
import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.acceptali.cli.org.getalp.lexsema.acceptali.acceptions.SenseCluster;
import org.getalp.lexsema.acceptali.cli.org.getalp.lexsema.acceptali.acceptions.SenseClusterer;
import org.getalp.lexsema.acceptali.cli.org.getalp.lexsema.acceptali.acceptions.SenseClustererImpl;
import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosureImpl;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureGenerator;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureGeneratorFactory;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureSemanticSignatureGenerator;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureSemanticSignatureGeneratorImpl;
import org.getalp.lexsema.acceptali.closure.similarity.PairwiseCLSimilarityMatrixGeneratorSim;
import org.getalp.lexsema.acceptali.closure.similarity.PairwiseCrossLingualSimilarityMatrixGenerator;
import org.getalp.lexsema.io.word2vec.MultilingualSerializedModelWord2VecLoader;
import org.getalp.lexsema.io.word2vec.MultilingualWord2VecLoader;
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
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureMatrixImplBuilder;
import org.getalp.lexsema.similarity.measures.crosslingual.TranslatorCrossLingualSimilarity;
import org.getalp.lexsema.similarity.signatures.enrichment.SignatureEnrichment;
import org.getalp.lexsema.similarity.signatures.enrichment.Word2VecSignatureEnrichment;
import org.getalp.lexsema.translation.BingAPITranslator;
import org.getalp.lexsema.translation.CachedTranslator;
import org.getalp.lexsema.translation.Translator;
import org.getalp.lexsema.util.Language;
import org.getalp.ml.matrix.factorization.TapkeeNLMatrixFactorization;
import org.getalp.ml.matrix.factorization.TapkeeNLMatrixFactorizationFactory;
import org.getalp.ml.matrix.filters.Filter;
import org.getalp.ml.matrix.filters.MatrixFactorizationFilter;
import org.getalp.ml.matrix.score.SumMatrixScorer;
import org.getalp.ml.optimization.org.getalp.util.Matrices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static java.io.File.separator;


public final class AcceptionClusteringExperimentGenerationSim {
    public static final String DB_PATH = String.format("%sVolumes%sRAMDisk", separator, separator);
    public static final String ONTOLOGY_PROPERTIES = String.format("data%sontology.properties", separator);
    public static final File CLOSURE_SAVE_PATH = new File(String.format("..%sdata%sclosure_river", separator, separator));
    public static final String MATRIX_PATH = ".." + separator + "data" + separator + "acception_matrices";
    public static final String SIM_MATRIX_PATH = String.format("%s%ssource.dat", MATRIX_PATH, separator);
    public static final String WORD_2_VEC_MODEL = String.format("..%sdata%sword2vec", File.separator, File.separator);
    public static final int NUMBER_OF_TOP_LEVEL_CLUSTERS = 30;
    public static final int SIMILARITY_DIMENSIONS = 20;
    public static final int CL_DIMENSIONS = 20;
    public static final int ENRICHMENT_SIZE = 20;
    /**
     * ainuros@outlook.com account
     */
    public static final String BING_APP_ID = "dbnary_hyper";
    public static final String BING_APP_KEY = "IecT6H4OjaWo3OtH2pijfeNIx1y1bML3grXz/Gjo/+w=";
    public static final int DEPTH = 1;
    static Language[] loadLanguages = {
            Language.FRENCH, Language.ENGLISH, Language.ITALIAN, Language.SPANISH,
            Language.PORTUGUESE, Language.BULGARIAN, Language.CATALAN, Language.FINNISH,
            Language.GERMAN, Language.RUSSIAN, Language.GREEK, Language.TURKISH
    };
    private static String dbPath = DB_PATH;



    private static int numberOfTopLevelClusters = NUMBER_OF_TOP_LEVEL_CLUSTERS;
    private static int similarityDimensions = SIMILARITY_DIMENSIONS;
    private static int clDimensions = CL_DIMENSIONS;
    private static int enrichmentSize = ENRICHMENT_SIZE;
    private static String word2VecModel = WORD_2_VEC_MODEL;
    private static Logger logger = LoggerFactory.getLogger(AcceptionClusteringExperimentGenerationSim.class);


    private AcceptionClusteringExperimentGenerationSim() {
        System.setProperty("org.slf4j.simpleLogger.showShortLogName", "true");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
    }


    public static void main(String[] args) throws IOException, NoSuchVocableException {
        try {

            loadProperties();

            logger.info("Generating or Loading Closure...");
            Set<Sense> closureSet =  generateTranslationClosureWithSignatures(instantiateDBNary());

            long matrix_time = System.currentTimeMillis();

            SimilarityMeasure similarityMeasure = createSimilarityMeasure();

            Translator translator = new CachedTranslator("Bing", new BingAPITranslator(BING_APP_ID, BING_APP_KEY));

            logger.info("Loading Word2Vec...");
            MultilingualWord2VecLoader word2VecLoader = new MultilingualSerializedModelWord2VecLoader();
            word2VecLoader.load(new File(WORD_2_VEC_MODEL));
            SignatureEnrichment signatureEnrichment = new Word2VecSignatureEnrichment(null, enrichmentSize);

            SimilarityMeasure crossLingualSimilarity =
                    new TranslatorCrossLingualSimilarity(similarityMeasure, translator, signatureEnrichment);
            /*CrossLingualSimilarity crossLingualSimilarity =
                    new TranslatorCrossLingualSimilarity(similarityMeasure, translator);*/

            logger.info("Generating matrix...");
            PairwiseCrossLingualSimilarityMatrixGenerator matrixGenerator =
                    new PairwiseCLSimilarityMatrixGeneratorSim(crossLingualSimilarity, closureSet);
            matrixGenerator.generateMatrix();

            logger.info("Clustering...");
            SenseClusterer clusterer = new SenseClustererImpl(new KMeans());
            DoubleMatrix2D inputData = matrixGenerator.getScoreMatrix();
            inputData.normalize();

            //Filter filter = new MatrixFactorizationFilter(new NonnegativeMatrixFactorizationKLFactory(),20);
            Filter filter2 = new MatrixFactorizationFilter(new TapkeeNLMatrixFactorizationFactory(TapkeeNLMatrixFactorization.Method.HLLE), clDimensions);

            //filter.apply(inputData);
            filter2.apply(inputData);

            List<SenseCluster> clusters = clusterer.cluster(inputData, numberOfTopLevelClusters, new ArrayList<>(closureSet));

            for (SenseCluster sc : clusters) {
                logger.info(sc.toString());
            }

            createMatrixDirectories(matrix_time);
            writeSourceMatrix(matrix_time, matrixGenerator.getScoreMatrix());

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                InstantiationException | ClassNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private static void loadProperties() {
        final Properties properties = new Properties();
        try (InputStream props = AcceptionClusteringExperimentGenerationSim.class.getResourceAsStream(separator + "acceptali.properties")) {
            if (props != null) {
                properties.load(props);
                if (properties.containsKey("acceptali.config.tdbPath")) {
                    dbPath = properties.getProperty("acceptali.config.tdbPath");
                    logger.info(String.format("[CONFIG] Loaded tdbPath=%s", dbPath));
                }
                if (properties.containsKey("acceptali.config.numberOfClusters")) {
                    numberOfTopLevelClusters = Integer.valueOf(properties.getProperty("acceptali.config.numberOfClusters"));
                    logger.info(String.format("[CONFIG] Loaded numberOfClusters=%d", numberOfTopLevelClusters));
                }
                if (properties.containsKey("acceptali.config.similarityDimensions")) {
                    similarityDimensions = Integer.valueOf(properties.getProperty("acceptali.config.similarityDimensions"));
                    logger.info(String.format("[CONFIG] Loaded similarityDimensions=%d", similarityDimensions));
                }
                if (properties.containsKey("acceptali.config.clDimensions")) {
                    clDimensions = Integer.valueOf(properties.getProperty("acceptali.config.clDimensions"));
                    logger.info(String.format("[CONFIG] Loaded clDimensions=%d", clDimensions));
                }
                if (properties.containsKey("acceptali.config.enrichmentSize")) {
                    enrichmentSize = Integer.valueOf(properties.getProperty("acceptali.config.enrichmentSize"));
                    logger.info(String.format("[CONFIG] Loaded enrichmentSize=%d", enrichmentSize));
                }
                if (properties.containsKey("acceptali.config.word2vecModel")) {
                    word2VecModel = properties.getProperty("acceptali.config.word2vecModel");
                    logger.info(String.format("[CONFIG] Loaded word2vecModel=%s", word2VecModel));
                }
            } else {
                logger.info("No acceptali.properties in the classpath, using default configuration.");
            }
        } catch (IOException e) {
            logger.info("No acceptali.properties in the classpath, using default configuration.");
        }
    }

    private static Set<Sense> flatSenseClosure(LexicalResourceTranslationClosure<Sense> closure) {
        return closure.senseFlatClosure();
    }

    @SuppressWarnings({"LawOfDemeter", "MagicNumber", "FeatureEnvy"})
    private static SimilarityMeasure createSimilarityMeasure() {
        return new TverskiIndexSimilarityMeasureMatrixImplBuilder()
                .computeRatio(true)
                .alpha(1d)
                .beta(0.5d)
                .gamma(0.5d)
                .fuzzyMatching(true)
                .isDistance(true)
                .matrixScorer(new SumMatrixScorer())
                .setDistance(new ScaledLevenstein())
                        //.filter(new NormalizationFilter())
                        //.filter(new MatrixFactorizationFilter(new TapkeeNLMatrixFactorizationFactory(Method.HLLE)))
                .build();

    }

    private static Set<Sense> generateTranslationClosureWithSignatures(DBNary dbNary) throws NoSuchVocableException {
        LexicalResourceTranslationClosure<LexicalSense> closure;

        if (CLOSURE_SAVE_PATH.exists()) {
            TranslationClosureGenerator gtc = TranslationClosureGeneratorFactory.createFileGenerator(dbNary, CLOSURE_SAVE_PATH.getAbsolutePath());
            closure = generateLexicalSenseClosure(gtc, DEPTH);
        } else {
            Vocable v = dbNary.getVocable("river", Language.ENGLISH);
            List<LexicalEntry> ventries = dbNary.getLexicalEntries(v);
            if (!ventries.isEmpty()) {
                TranslationClosureGenerator gtc = TranslationClosureGeneratorFactory.createVocablePOSGenerator(v, "http://www.lexinfo.net/ontology/2.0/lexinfo#noun", dbNary);
                closure = generateLexicalSenseClosure(gtc, DEPTH);
            } else {
                closure = new LexicalResourceTranslationClosureImpl();
            }
        }
        TranslationClosureSemanticSignatureGenerator semanticSignatureGenerator =
                new TranslationClosureSemanticSignatureGeneratorImpl();
        LexicalResourceTranslationClosure<Sense> sigClosure = semanticSignatureGenerator.generateSemanticSignatures(closure);
        logger.info(sigClosure.toString());
        return flatSenseClosure(sigClosure);
    }

    private static LexicalResourceTranslationClosure<LexicalSense> generateLexicalSenseClosure(TranslationClosureGenerator ctg, int degree) {
        return ctg.generateClosure(degree);
    }

    private static DBNary instantiateDBNary() throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Store vts = new JenaTDBStore(dbPath);
        StoreHandler.registerStoreInstance(vts);
        //StoreHandler.DEBUG_ON = true;
        OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        // Creating DBNary wrapper
        return (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, loadLanguages);
    }

    private static void writeSourceMatrix(long matrix_time, DoubleMatrix2D matrix) {
        try (PrintWriter pw = new PrintWriter(MATRIX_PATH + separator + matrix_time + separator + "source.dat")) {
            Matrices.matrixCSVWriter(pw, matrix);
            pw.close();
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private static void createMatrixDirectories(long matrix_time) {
        File dir = new File(MATRIX_PATH + separator + matrix_time);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (!result) {
                logger.error("Cannot create " + MATRIX_PATH + separator + matrix_time);
            }
        }
    }

}
