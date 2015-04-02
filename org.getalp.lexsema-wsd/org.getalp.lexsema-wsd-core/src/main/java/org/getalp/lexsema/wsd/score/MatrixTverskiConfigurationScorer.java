package org.getalp.lexsema.wsd.score;


import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.util.caching.Cache;
import org.getalp.lexsema.util.caching.CachePool;
import org.getalp.lexsema.util.dataitems.Triple;
import org.getalp.lexsema.util.dataitems.TripleImpl;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.ml.matrix.filters.Filter;
import org.getalp.ml.matrix.score.MatrixScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MatrixTverskiConfigurationScorer implements ConfigurationScorer {

    private static Logger logger = LoggerFactory.getLogger(MatrixTverskiConfigurationScorer.class);
    private SimilarityMeasure similarityMeasure;
    private ExecutorService threadPool;
    private List<Future<Triple<Integer, Integer, Double>>> completeTasks;
    private List<EntryScoreCallable> tasks;
    private Filter filter;
    private MatrixScorer matrixScorer;
    private Cache cache;

    public MatrixTverskiConfigurationScorer(SimilarityMeasure similarityMeasure, Filter filter, MatrixScorer matrixScorer, int numberThreads) {
        this.similarityMeasure = similarityMeasure;
        tasks = new ArrayList<>();
        threadPool = Executors.newFixedThreadPool(numberThreads);
        this.filter = filter;
        this.matrixScorer = matrixScorer;
        cache = CachePool.getResource();
    }

    private String generateKey(int index1, int index2) {
        return String.format("sim____%s____%d____%d", similarityMeasure.toString(), index1, index2);
    }

    @Override
    public double computeScore(Document d, Configuration c) {
        DoubleMatrix2D scoreMatrix = new DenseDoubleMatrix2D(c.size(), c.size());
        for (int i = 0; i < c.size(); i++) {
            for (int j = 0; j < c.size(); j++) {


                String key_ij = generateKey(i, j);
                String key_ji = generateKey(j, i);
                String value_ij = cache.get(key_ij);
                String value_ji = cache.get(key_ji);

                if (value_ij != null || value_ji != null) {
                    double score;
                    if (value_ij != null) {
                        score = Double.valueOf(value_ij);
                    } else {
                        score = Double.valueOf(value_ji);
                    }
                    scoreMatrix.setQuick(i, j, score);
                } else {
                    try {
                        tasks.add(new EntryScoreCallable(i, j, d, c));
                    } catch (RejectedExecutionException e) {
                        logger.debug("Thread pool rejected task " + i);
                    }
                }
            }
        }

        try {
            completeTasks = threadPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            logger.error(e.getLocalizedMessage());
        }
        tasks.clear();
        boolean progressChecked = false;
        while (!progressChecked || !completeTasks.isEmpty()) {
            for (int i = 0; i < completeTasks.size(); ) {
                Future<Triple<Integer, Integer, Double>> current = completeTasks.get(i);
                if (current.isDone()) {
                    try {
                        //noinspection LocalVariableOfConcreteClass
                        Triple<Integer, Integer, Double> pair = current.get();
                        int indexA = pair.first();
                        int indexB = pair.second();
                        double value = pair.third();
                        cache.set(generateKey(indexA, indexB), String.valueOf(value));
                        cache.set(generateKey(indexB, indexA), String.valueOf(value));
                        scoreMatrix.setQuick(indexA, indexB, value);
                    } catch (InterruptedException e) {
                        logger.debug("Interrupted in configuration score entry calculation" + e.getLocalizedMessage());
                    } catch (ExecutionException e) {
                        logger.debug("ExecutionException in configuration score entry calculation " + e.getLocalizedMessage());
                    }
                    completeTasks.remove(i);
                } else {
                    //noinspection AssignmentToForLoopParameter
                    i++;
                }
            }
            progressChecked = true;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (filter != null) {
            scoreMatrix = filter.apply(scoreMatrix);
        }

        return matrixScorer.computeScore(scoreMatrix);
    }

    @Override
    public void release() {
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.debug(e.getLocalizedMessage());
        }
    }


    private class EntryScoreCallable implements Callable<Triple<Integer, Integer, Double>> {

        private int indexA;
        private int indexB;
        private Document document;
        private Configuration configuration;

        public EntryScoreCallable(int indexA, int indexB, Document document, Configuration configuration) {
            this.document = document;
            this.configuration = configuration;
            this.indexA = indexA;
            this.indexB = indexB;
        }

        @Override
        public Triple<Integer, Integer, Double> call() throws Exception {
            try {
                Sense a = document.getSenses(indexA).get(configuration.getAssignment(indexA));
                Sense b = document.getSenses(indexB).get(configuration.getAssignment(indexB));
                double sim = similarityMeasure.compute(a.getSemanticSignature(), b.getSemanticSignature(), null, null);
                return new TripleImpl<>(indexA, indexB, sim);
            } catch (RuntimeException e) {
                logger.error(e.getLocalizedMessage());
            }
            return new TripleImpl<>(indexA, indexB, 0d);
        }
    }

}
