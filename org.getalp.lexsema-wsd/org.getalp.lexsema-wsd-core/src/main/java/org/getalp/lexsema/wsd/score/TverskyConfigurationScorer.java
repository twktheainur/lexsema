package org.getalp.lexsema.wsd.score;

import edu.stanford.nlp.util.Pair;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.optimization.functions.Function;
import org.getalp.optimization.functions.input.FunctionInput;
import org.getalp.optimization.functions.setfunctions.input.ValueListInput;
import org.getalp.optimization.functions.setfunctions.submodular.Sum;
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
    private List<Future<Pair<Integer, Double>>> runningTasks;

    public TverskyConfigurationScorer(SimilarityMeasure similarityMeasure, int numberThreads) {
        this.similarityMeasure = similarityMeasure;
        runningTasks = new ArrayList<>();
        threadPool = Executors.newFixedThreadPool(numberThreads);
    }

    @Override
    public double computeScore(Document d, Configuration c) {
        Double[] scores = new Double[c.size()];
        for (int i = 0; i < c.size(); i++) {
            runningTasks.add(threadPool.submit(new EntryScoreCallable(i, d, c)));
        }

        boolean progressChecked = false;
        while (!progressChecked || !runningTasks.isEmpty()) {
            for (int i = 0; i < runningTasks.size(); ) {
                Future<Pair<Integer, Double>> current = runningTasks.get(i);
                if (current.isDone()) {
                    try {
                        Pair<Integer, Double> pair = current.get();
                        int index = pair.first();
                        double value = pair.second();
                        scores[index] = value;
                    } catch (InterruptedException e) {
                        logger.debug("Interrupted in configuration score entry calculation" + e.getLocalizedMessage());
                    } catch (ExecutionException e) {
                        logger.debug("ExecutionException in configuration score entry calculation " + e.getLocalizedMessage());
                    }
                    runningTasks.remove(i);
                } else {
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

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Function sum = new Sum(1);
        FunctionInput valueListInput = new ValueListInput(Arrays.asList(scores), false, true);
        return sum.F(valueListInput);
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
            FunctionInput input = new ConfigurationEntryPairwiseScoreInput(configuration, document, index,
                    similarityMeasure, true);
            Function sum = new Sum(1);
            return new Pair<>(index, sum.F(input));
        }
    }

}
