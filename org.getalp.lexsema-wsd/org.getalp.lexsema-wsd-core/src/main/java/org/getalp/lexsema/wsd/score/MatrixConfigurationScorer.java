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

public class MatrixConfigurationScorer implements ConfigurationScorer {

    private static final Logger logger = LoggerFactory.getLogger(MatrixConfigurationScorer.class);
    private final SimilarityMeasure similarityMeasure;
    private final ExecutorService threadPool;
    private List<Future<Triple<Integer, Integer, Double>>> completeTasks;
    private final List<EntryScoreCallable> tasks;
    private final Filter filter;
    private final MatrixScorer matrixScorer;
    private double[][][][] cache;
    private Document currentDocument;

    public MatrixConfigurationScorer(SimilarityMeasure similarityMeasure, MatrixScorer matrixScorer, int numberThreads) {
        this(similarityMeasure, null, matrixScorer, numberThreads);
    }

    public MatrixConfigurationScorer(SimilarityMeasure similarityMeasure, Filter filter, MatrixScorer matrixScorer, int numberThreads) {
        this.similarityMeasure = similarityMeasure;
        tasks = new ArrayList<>();
        threadPool = Executors.newFixedThreadPool(numberThreads);
        this.filter = filter;
        this.matrixScorer = matrixScorer;
    }

    private String generateKey(int index1, int index2) {
        return String.format("sim____%s____%d____%d", similarityMeasure.toString(), index1, index2);
    }

    private void initCache(Document d) {
        if (currentDocument != d) {
            cache = new double[d.size()][d.size()][][];
            for (int i = 0; i < d.size(); i++) {
                for (int j = i + 1; j < d.size(); j++) {
                    cache[i][j] = new double[d.getSenses(i).size()][d.getSenses(j).size()];
                    for (int k = 0; k < d.getSenses(i).size(); k++) {
                        for (int l = 0; l < d.getSenses(j).size(); l++) {
                            cache[i][j][k][l] = -1;
                        }
                    }
                }
            }
            currentDocument = d;
        }
    }

    @Override
    public double computeScore(Document d, Configuration c) {
        DoubleMatrix2D scoreMatrix = new DenseDoubleMatrix2D(c.size(), c.size());
        scoreMatrix.assign(0);
        initCache(d);
        for (int i = 0; i < c.size(); i++) {
            for (int j = i; j < c.size(); j++) {
                try {
                    tasks.add(new EntryScoreCallable(i, j, d, c));
                } catch (RejectedExecutionException e) {
                    logger.debug("Thread pool rejected task {} -- {}", i,e.getLocalizedMessage());
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
                        scoreMatrix.setQuick(indexA, indexB, value);
                    } catch (InterruptedException e) {
                        logger.debug("Interrupted in configuration score entry calculation {}", e.getLocalizedMessage());
                    } catch (ExecutionException e) {
                        logger.debug("ExecutionException in configuration score entry calculation {}", e.getLocalizedMessage());
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


    private final class EntryScoreCallable implements Callable<Triple<Integer, Integer, Double>> {

        private final int indexA;
        private final int indexB;
        private final Document document;
        private final Configuration configuration;

        private EntryScoreCallable(int indexA, int indexB, Document document, Configuration configuration) {
            this.document = document;
            this.configuration = configuration;
            this.indexA = indexA;
            this.indexB = indexB;
        }

        @Override
        public Triple<Integer, Integer, Double> call() throws Exception {
            //try {

            int senseA = configuration.getAssignment(indexA);
            int senseB = configuration.getAssignment(indexB);
            int start = configuration.getStart();
            double value = cache[indexA][indexB][senseA][senseB];
            if(value<0){
                Sense a = document.getSenses(start, indexA).get(senseA);
                Sense b = document.getSenses(start, indexB).get(senseB);
                value = a.computeSimilarityWith(similarityMeasure, b);
            }
            return new TripleImpl<>(indexA, indexB, value);
            //} catch (RuntimeException e) {
            //   logger.error(e.getLocalizedMessage());
            // }
            //return new TripleImpl<>(indexA, indexB, 0d);
        }
    }

}
