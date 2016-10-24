package org.getalp.lexsema.axalign.experiments;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.lexsema.axalign.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.axalign.closure.generator.TranslationClosureGenerator;
import org.getalp.lexsema.axalign.closure.generator.TranslationClosureGeneratorFactory;
import org.getalp.lexsema.axalign.closure.generator.TranslationClosureSemanticSignatureGenerator;
import org.getalp.lexsema.axalign.closure.generator.TranslationClosureSemanticSignatureGeneratorImpl;
import org.getalp.lexsema.axalign.closure.similarity.PairwiseCLSimilarityMatrixGeneratorFile;
import org.getalp.lexsema.axalign.closure.similarity.PairwiseSimilarityMatrixGenerator;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.io.File.separator;


public final class AcceptionClusteringExperimentFromFile {
    private static final String DB_PATH = String.format("%sVolumes%sRAMDisk", separator, separator);
    public static final String ONTOLOGY_PROPERTIES = String.format("data%sontology.properties", separator);
    private static final File CLOSURE_SAVE_PATH = new File(String.format("..%sdata%sclosure_river", separator, separator));
    private static final String MATRIX_PATH = ".." + separator + "data" + separator + "acception_matrices";
    private static final String SIM_MATRIX_PATH = String.format("%s%ssource.dat", MATRIX_PATH, separator);

    public static final int DEPTH = 1;
    private static final double CLUSTERING_THRESHOLD = .5;

    private static final double THETA = .8d;

    private static Language[] loadLanguages = {
            Language.FRENCH, Language.ENGLISH, Language.ITALIAN, Language.SPANISH,
            Language.PORTUGUESE, Language.BULGARIAN, Language.CATALAN, Language.FINNISH,
            Language.GERMAN, Language.RUSSIAN, Language.GREEK, Language.TURKISH
    };
    private static final Logger logger = LoggerFactory.getLogger(AcceptionClusteringExperimentFromFile.class);


    private AcceptionClusteringExperimentFromFile() {
        System.setProperty("org.slf4j.simpleLogger.showShortLogName", "true");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
    }


    public static void main(String... args) throws IOException, NoSuchVocableException {


        try {
            List<Sense> closureSet = new ArrayList<>(generateTranslationClosureWithSignatures(instantiateDBNary()));
            PairwiseSimilarityMatrixGenerator matrixGenerator =
                    new PairwiseCLSimilarityMatrixGeneratorFile(SIM_MATRIX_PATH);
            matrixGenerator.generateMatrix();

            DoubleMatrix2D scoreMatrix = matrixGenerator.getScoreMatrix();


            for(int i=0; i<scoreMatrix.rows();i++){
                for(int j=0; j<scoreMatrix.columns(); j++){
                    if(i!=j && closureSet.get(i).getLanguage() != closureSet.get(j).getLanguage()){
                        logger.info("[{}] |{}--{}|", scoreMatrix.get(i,j), closureSet.get(i).toString(),closureSet.get(j).getId());
                    }
                }
            }

            //logger.info(matrixGenerator.getScoreMatrix().toString());
            //SenseClusterer clusterer = new TricklSenseClusterer(new FuzzyCMeans(), CLUSTERING_THRESHOLD);
            //clusterer.setKernelFilter(new NeuralICAMatrixFactorizationFilter(5));
            //DoubleMatrix2D inputData = matrixGenerator.getScoreMatrix();
            //inputData.normalize();
            //List<SenseCluster> clusters = clusterer.cluster(inputData, 10, new ArrayList<>(closureSet));

            /*for(SenseCluster sc: clusters){
                logger.info(sc.toString());
            }*/

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                InstantiationException | ClassNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    private static DBNary instantiateDBNary() throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Store vts = new JenaTDBStore(DB_PATH);
        StoreHandler.registerStoreInstance(vts);
        //StoreHandler.DEBUG_ON = true;
        OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        // Creating DBNary wrapper
        return (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, loadLanguages);
    }

    private static Set<Sense> generateTranslationClosureWithSignatures(DBNary dbNary) throws NoSuchVocableException {
        LexicalResourceTranslationClosure<LexicalSense> closure;

        TranslationClosureGenerator gtc = TranslationClosureGeneratorFactory
                .createFileGenerator(dbNary, CLOSURE_SAVE_PATH.getAbsolutePath());
        closure = generateLexicalSenseClosure(gtc, DEPTH);

        TranslationClosureSemanticSignatureGenerator semanticSignatureGenerator =
                new TranslationClosureSemanticSignatureGeneratorImpl();

        return flatSenseClosure(semanticSignatureGenerator.generateSemanticSignatures(closure));
    }

    private static Set<Sense> flatSenseClosure(LexicalResourceTranslationClosure<Sense> closure){
        return closure.senseFlatClosure();
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
