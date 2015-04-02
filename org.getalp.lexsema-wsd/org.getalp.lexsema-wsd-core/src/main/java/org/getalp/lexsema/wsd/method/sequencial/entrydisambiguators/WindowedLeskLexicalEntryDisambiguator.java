package org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.SubConfiguration;
import org.getalp.lexsema.wsd.method.sequencial.parameters.WindowedLeskParameters;
import org.getalp.lexsema.wsd.score.ConfigurationEntryPairwiseScoreInput;
import org.getalp.ml.optimization.functions.Function;
import org.getalp.ml.optimization.functions.input.FunctionInput;
import org.getalp.ml.optimization.functions.setfunctions.submodular.Sum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class WindowedLeskLexicalEntryDisambiguator extends SequentialLexicalEntryDisambiguator {

    private final static Logger logger = LoggerFactory.getLogger(WindowedLeskLexicalEntryDisambiguator.class);

    WindowedLeskParameters params;
    SimilarityMeasure similarityMeasure;

    public WindowedLeskLexicalEntryDisambiguator(Configuration c, Document d, SimilarityMeasure sim,
                                                 WindowedLeskParameters params,
                                                 int start, int end, int currentIndex) {
        super(c, d, start, end, currentIndex);
        this.params = params;
        similarityMeasure = sim;
    }

    @Override
    public void run() {
        try {
            SubConfiguration nsc = new SubConfiguration(getDocument(), getStart(), getEnd());
            List<SubConfiguration> combinations = getCombinations(0, nsc, getDocument());
            int selected = -1;
            if (numberOfSenses(getCurrentIndex()) == 1) {
                selected = 0;
            } else {
                int minIndex = -1;
                int maxIndex = -1;
                double minValue;
                double prevMinValue;
                double maxValue;
                double prevMaxValue;

                minValue = Double.MAX_VALUE;
                prevMinValue = 0;

                maxValue = -Double.MAX_VALUE;
                prevMaxValue = 0;

                List<Double> results = new ArrayList<>();
                Collection<Integer> candidates = new ArrayList<>();
                List<Configuration> configurations = new ArrayList<>();
                for (int cmb = 0; cmb < combinations.size(); cmb++) {
                    //String wordProgress = String.format("%.2f%%", (100d * ((double) cmb / (double) combinations.size())));
                    //System.err.print( progress + " ==> "+currentEntry.getLemma()+"#"+ currentEntry.getPos() + " ["+cmb+"/"+combinations.size()+" | "+wordProgress +"]\r");
                    Configuration sc = combinations.get(cmb);

                    FunctionInput sci = new ConfigurationEntryPairwiseScoreInput(sc, getDocument(),
                            getCurrentIndex() - sc.getStart(), similarityMeasure);
                    Function f = new Sum(1);
                    double score = f.F(sci) / sc.size();
                    //System.err.println(score);
                    if (score <= minValue) {
                        prevMinValue = minValue;
                        minValue = score;
                        minIndex = cmb;
                    }
                    if (score >= maxValue) {
                        prevMaxValue = maxValue;
                        maxValue = score;
                        maxIndex = cmb;
                    }
                    results.add(score);
                    configurations.add(sc);
                }


                double range = maxValue - minValue;
                double threshold = .05 * range;
                Map<Integer, Integer> candidateHistogram = new HashMap<>();
                for (int i = 0; i < results.size(); i++) {
                    if (Math.abs(results.get(i) - maxValue) < threshold) {
                        int assignment = configurations.get(i).getAssignment(getCurrentIndex() - combinations.get(minIndex).getStart());
                        if (!candidateHistogram.containsKey(assignment)) {
                            candidateHistogram.put(assignment, 0);
                        } else {
                            candidateHistogram.put(assignment, candidateHistogram.get(assignment) + 1);
                        }

                    }
                }
                int maxSensIndex = 0;
                int senseCount = -1;
                for (int sense : candidateHistogram.keySet()) {
                    if (candidateHistogram.get(sense) > senseCount) {
                        senseCount = candidateHistogram.get(sense);
                        maxSensIndex = sense;

                    }
                }


                /*if (params.isMinimize() && minIndex == -1 || !params.isMinimize() && maxIndex == -1) {
                    selected = -1;
                } else if (params.isMinimize()) {
                    selected = combinations.get(minIndex).getAssignment(getCurrentIndex() - combinations.get(minIndex).getStart());
                } else {
                    selected = combinations.get(maxIndex).getAssignment(getCurrentIndex() - combinations.get(maxIndex).getStart());
                }*/

                selected = maxSensIndex;

                if (params.isFallbackFS() && selected == -1) {
                    selected = 0;
                }
            }
            getConfiguration().setSense(getCurrentIndex(), selected);
            getConfiguration().setConfidence(getCurrentIndex(), 1d);
        } catch (RuntimeException ex) {
            //ex.printStackTrace();
            logger.error(ex.getLocalizedMessage());
            System.exit(1);
        }
    }

    private List<SubConfiguration> getCombinations(int currentIndex, SubConfiguration s, Document d) {
        List<SubConfiguration> combinations = new ArrayList<>();
        if (currentIndex < s.size()) {
            for (int i = 0; i < d.getSenses(s.getStart(), currentIndex).size(); i++) {
                SubConfiguration newSub = new SubConfiguration(s);
                newSub.setSense(currentIndex, i);
                combinations.addAll(getCombinations(currentIndex + 1, newSub, d));
            }

        } else {
            combinations.add(s);
        }
        return combinations;
    }
}
