package org.getalp.lexsema.wsd.score;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.util.dataitems.Pair;
import org.getalp.lexsema.util.dataitems.PairImpl;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.ml.optimization.functions.Function;
import org.getalp.ml.optimization.functions.input.FunctionInput;
import org.getalp.ml.optimization.functions.setfunctions.input.ValueListInput;
import org.getalp.ml.optimization.functions.setfunctions.submodular.Sum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class TverskyConfigurationScorer implements ConfigurationScorer {

    private static Logger logger = LoggerFactory.getLogger(TverskyConfigurationScorer.class);
    private SimilarityMeasure similarityMeasure;
    private ExecutorService threadPool;
    private List<Future<Pair<Integer, Double>>> completeTasks;
    private List<EntryScoreCallable> tasks;

    public TverskyConfigurationScorer(SimilarityMeasure similarityMeasure, int numberThreads) {
        this.similarityMeasure = similarityMeasure;
        tasks = new ArrayList<>();
        threadPool = Executors.newFixedThreadPool(numberThreads);
    }

    @Override
    public double computeScore(Document d, Configuration c) {
        Double[] scores = new Double[c.size()];
        for (int i = 0; i < c.size(); i++) {
            try {
                tasks.add(new EntryScoreCallable(i, d, c));
                //threadPool.submit();
                //completeTasks.add(threadPool.submit(new EntryScoreCallable(i, d, c)));
            } catch (RejectedExecutionException e) {
                logger.debug("Threadpool rejected task " + i);
            }
        }

        try {
            completeTasks = threadPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tasks.clear();
        boolean progressChecked = false;
        while (!progressChecked || !completeTasks.isEmpty()) {
            for (int i = 0; i < completeTasks.size(); ) {
                Future<Pair<Integer, Double>> current = completeTasks.get(i);
                if (current.isDone()) {
                    try {
                        //noinspection LocalVariableOfConcreteClass
                        Pair<Integer, Double> pair = current.get();
                        int index = pair.first();
                        double value = pair.second();
                        scores[index] = value;
                        //System.err.println(value);
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
        Function sum = new Sum(1);
        FunctionInput valueListInput = new ValueListInput(Arrays.asList(scores), false, true);
        return sum.F(valueListInput);
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


    private class EntryScoreCallable implements Callable<Pair<Integer, Double>> {

        private int index;
        private Document document;
        private Configuration configuration;

        public EntryScoreCallable(int index, Document document, Configuration configuration) {
            this.index = index;
            this.document = document;
            this.configuration = configuration;
        }

        @Override
        public Pair<Integer, Double> call() throws Exception {
            try {
                FunctionInput input = new ConfigurationEntryPairwiseScoreInput(configuration, document, index,
                        similarityMeasure, true);
                Function sum = new Sum(1);
                return new PairImpl<>(index, sum.F(input));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new PairImpl<>(index, 0d);
        }
    }

}
