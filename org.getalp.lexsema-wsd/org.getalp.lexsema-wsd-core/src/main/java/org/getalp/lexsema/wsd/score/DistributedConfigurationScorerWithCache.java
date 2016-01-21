package org.getalp.lexsema.wsd.score;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;

public class DistributedConfigurationScorerWithCache implements ConfigurationScorer {
    private final SimilarityMeasure similarityMeasure;
    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private final JavaSparkContext sparkContext;

    private double[][][][] cache;

    private Document currentDocument;

    public DistributedConfigurationScorerWithCache(SimilarityMeasure similarityMeasure, String sparkMaster) {
        this.similarityMeasure = similarityMeasure;
        int nbThreads = Runtime.getRuntime().availableProcessors();@SuppressWarnings("LocalVariableOfConcreteClass") final SparkConf sparkConf = new SparkConf().setAppName("DistributedConfigurationScorerWithCache");
        //noinspection LawOfDemeter
        sparkContext = new JavaSparkContext(sparkConf.setMaster(sparkMaster));
    }

    private void initializeCache(Document document){
        int documentSize = document.size();
        cache = new double[documentSize][documentSize][][];
        for (int i = 0; i < documentSize; i++) {
            for (int j = i + 1; j < documentSize; j++) {
                int sizeFirst = document.numberOfSensesForWord(i);
                int sizeSecond = document.numberOfSensesForWord(j);
                cache[i][j] = new double[sizeFirst][sizeSecond];
                for (int k = 0; k < sizeFirst; k++) {
                    for (int senseIndex = 0; senseIndex < sizeSecond; senseIndex++) {
                        cache[i][j][k][senseIndex] = -1;
                    }
                }
            }
        }
    }


    public double computeScore(Document document, Configuration configuration) {
        if (currentDocument != document) {
           initializeCache(document);
            currentDocument = document;
        }

        List<IntermediateScorer> scorers = new ArrayList<>();
        for (int i = 0; i < configuration.size(); i++) {
            scorers.add(new IntermediateScorer(i, document, configuration));
        }

        //noinspection LocalVariableOfConcreteClass
        JavaRDD<IntermediateScorer> distributedScorers = sparkContext.parallelize(scorers);
        //noinspection LawOfDemeter
        return distributedScorers.mapToDouble(IntermediateScorer::call).sum();
    }

    private final class IntermediateScorer implements Callable<Double> {
        private final int i;

        private final Document document;

        private final Configuration configuration;

        private IntermediateScorer(int i, Document document, Configuration configuration) {
            this.i = i;
            this.document = document;
            this.configuration = configuration;
        }

        @Override
        public Double call() {
            double score = 0;
            int k = configuration.getAssignment(i);
            if (k >= 0 && !document.getSenses(i).isEmpty()) {
                Sense senseA = document.getSenses(i).get(k);
                for (int j = i + 1; j < configuration.size(); j++) {
                    int assignment = configuration.getAssignment(j);
                    if (assignment >= 0 && !document.getSenses(j).isEmpty()) {
                        double cacheCell = cache[i][j][k][assignment];
                        if (cacheCell > -1) {
                            score += cacheCell;
                        } else {
                            Sense senseB = document.getSenses(j).get(assignment);
                            double similarity = senseA.computeSimilarityWith(similarityMeasure, senseB);
                            score += similarity;
                            cache[i][j][k][assignment] = similarity;
                        }
                    } else {
                        return 0d;
                    }
                }
            }
            return score;
        }
    }

    @Override
    public void release() {
    }
}
