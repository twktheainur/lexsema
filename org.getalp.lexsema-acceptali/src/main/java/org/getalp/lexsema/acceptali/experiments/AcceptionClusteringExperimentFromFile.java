package org.getalp.lexsema.acceptali.experiments;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import com.trickl.cluster.ClusterAlgorithm;
import com.trickl.cluster.KernelKMeans;
import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosureImpl;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureGenerator;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureGeneratorFactory;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureSemanticSignatureGenerator;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureSemanticSignatureGeneratorImpl;
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
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.TverskiIndexSimilarityMeasureMatrixImplBuilder;
import org.getalp.ml.matrix.factorization.*;
import org.getalp.ml.matrix.filters.MatrixFactorizationFilter;
import org.getalp.ml.matrix.score.SumMatrixScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import static java.io.File.separator;


public final class AcceptionClusteringExperimentFromFile {
    public static final String DB_PATH = String.format("%sVolumes%sRAMDisk", separator, separator);
    public static final String ONTOLOGY_PROPERTIES = String.format("data%sontology.properties", separator);
    public static final File CLOSURE_SAVE_PATH = new File(String.format("..%sdata%sclosure_river", separator, separator));
    public static final String MATRIX_PATH = ".." + separator + "data" + separator + "acception_matrices";
    public static final String TRANSLATION_DB_PATH = String.format("..%sdata%stranslatorDB", separator, separator);
    public static final String SIM_MATRIX_PATH = String.format("%s%ssource.dat",MATRIX_PATH,separator);
    /**
     * twk.theainur@live.co.uk account
     */
    //public static final String BING_APP_ID = "dbnary";
    //public static final String BING_APP_KEY = "H2pC+d3b0L0tduSZzRafqPZyV6zmmzMmj9+AEpc9b1E=";

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
    private static Logger logger = LoggerFactory.getLogger(AcceptionClusteringExperimentFromFile.class);


    private AcceptionClusteringExperimentFromFile() {
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

            LexicalResourceTranslationClosure<Sense> closureWithSignatures = generateTranslationClosureWithSignatures(dbNary);

            //SimilarityMeasure similarityMeasure = createSimilarityMeasure();

            Set<Sense> closureSet = closureWithSignatures.senseFlatClosure();
            logger.info(closureWithSignatures.toString());

            long matrix_time = System.currentTimeMillis();
            //createMatrixDirectories(matrix_time);
            //writeTextClosure(closureWithSignatures, matrix_time);
            //printMatrixLabels(matrix_time, closureSet);

            //DoubleMatrix2D similarityMatrix = computeSimilarityMatrix(closureSet, similarityMeasure);
            DoubleMatrix2D similarityMatrix = loadSimilarityMatrix(closureSet, SIM_MATRIX_PATH);

            //writeSourceMatrix(matrix_time, similarityMatrix);

            ClusterAlgorithm clusterAlgorithm = new KernelKMeans();

            /**
             * Factorisation method
             */
            logger.info("Clustering with PSVD...");
            MatrixFactorization mf = new PartialSingularValueDecompositionFactory().factorize(similarityMatrix);
            cluster(mf.getV(),clusterAlgorithm,3,matrix_time,"PSVD");
            mf = new NeuralICAMatrixFactorization(similarityMatrix);
            mf.compute();
            logger.info("Clustering with NICA...");
            cluster(mf.getV(), clusterAlgorithm, 3, matrix_time, "NICA");
            mf = new LocalNonnegativeMatrixFactorizationFactory().factorize(similarityMatrix);
            logger.info("Clustering with LNMF...");
            cluster(mf.getV(), clusterAlgorithm, 3, matrix_time, "LNMF");
            mf = new NonnegativeMatrixFactorizationEDFactory().factorize(similarityMatrix);
            logger.info("Clustering with NMFED...");
            cluster(mf.getV(), clusterAlgorithm, 3, matrix_time, "NMFED");
            mf = new NonnegativeMatrixFactorizationKLFactory().factorize(similarityMatrix);
            logger.info("Clustering with NMFKL...");
            cluster(mf.getV(), clusterAlgorithm, 3, matrix_time, "NMFKL");
            //logger.info("Clustering with FICA...");
            //mf = new FastICAMatrixFactorizationFactory().factorize(similarityMatrix);
            //cluster(mf.getV(), clusterAlgorithm, 10, matrix_time, "FICA");
            //writeProjectedMatrix(matrix_time, mf.getV());
            //writeMixingMatrix(matrix_time, mf.getU());






        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                InstantiationException | ClassNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private static void cluster(DoubleMatrix2D input,ClusterAlgorithm clusterAlgorithm, int numberOfClusters,long matrix_time, String name){
        clusterAlgorithm.cluster(input, 10);
        DoubleMatrix2D assignments = clusterAlgorithm.getPartition();
        //writeAssignmentMatrix(matrix_time, assignments,name);
        logger.info(assignments.toString());
    }

    @SuppressWarnings({"LawOfDemeter", "MagicNumber", "FeatureEnvy"})
    private static SimilarityMeasure createSimilarityMeasure() {
        return new TverskiIndexSimilarityMeasureMatrixImplBuilder()
                .computeRatio(true)
                .alpha(1d)
                .beta(0.5d)
                .gamma(0.5d)
                .fuzzyMatching(true)
                .isDistance(false)
                .matrixScorer(new SumMatrixScorer())
                .setDistance(new ScaledLevenstein())
                .filter(new MatrixFactorizationFilter(new NeuralICAMAtrixFactoizationFactory()))
                .build();

    }

    private static LexicalResourceTranslationClosure<Sense> generateTranslationClosureWithSignatures(DBNary dbNary) throws NoSuchVocableException {
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

        TranslationClosureSemanticSignatureGenerator semanticSignatureGenerator = new TranslationClosureSemanticSignatureGeneratorImpl();
        return semanticSignatureGenerator.generateSemanticSignatures(closure);
    }

    private static LexicalResourceTranslationClosure<LexicalSense> generateLexicalSenseClosure(TranslationClosureGenerator ctg, int degree) {
        return ctg.generateClosure(degree);
    }
/*
    private static void writeTextClosure(LexicalResourceTranslationClosure<Sense> closureWithSignatures, long matrix_time) {
        try (PrintWriter pw = new PrintWriter(MATRIX_PATH + separator + matrix_time + separator + "text_closure.txt")) {
            //pw.println(similarityMatrix.toString());
            pw.println(closureWithSignatures.toString());
            pw.close();
            pw.close();
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private static void printMatrixLabels(long matrix_time, Iterable<Sense> closureSet) {
        try (PrintWriter pw = new PrintWriter(MATRIX_PATH + separator + matrix_time + separator + "labels.txt")) {
            for (Sense a : closureSet) {
                pw.println(URIUtils.getCanonicalURI(a.getId()));
            }
            pw.flush();
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

    private static void writeAssignmentMatrix(long matrix_time, DoubleMatrix2D matrix, String suffix) {
        try (PrintWriter pw = new PrintWriter(String.format("%s%s%d%sassignment_%s.dat", MATRIX_PATH, separator, matrix_time, separator, suffix))) {
            Matrices.matrixCSVWriter(pw, matrix);
            pw.close();
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private static void writeSourceMatrix(long matrix_time, DoubleMatrix2D matrix) {
        try (PrintWriter pw = new PrintWriter(MATRIX_PATH + separator + matrix_time + separator + "source.dat")) {
            Matrices.matrixCSVWriter(pw, matrix);
            pw.close();
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private static void writeMixingMatrix(long matrix_time, DoubleMatrix2D matrix) {
        try (PrintWriter pw = new PrintWriter(MATRIX_PATH + separator + matrix_time + separator + "mixing.dat")) {
            Matrices.matrixCSVWriter(pw, matrix);
            pw.close();
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private static void writeProjectedMatrix(long matrix_time, DoubleMatrix2D matrix) {
        try (PrintWriter pw = new PrintWriter(MATRIX_PATH + separator + matrix_time + separator + "projected.dat")) {
            Matrices.matrixCSVWriter(pw, matrix);
            pw.close();
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
    }
    */


}
