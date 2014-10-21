package org.getalp.disambiguation.method.legacy;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.LexicalEntry;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.configuration.SubConfiguration;
import org.getalp.disambiguation.method.Disambiguator;
import org.getalp.optimisation.functions.Function;
import org.getalp.optimisation.functions.setfunctions.input.SenseCombinationInput;
import org.getalp.optimisation.functions.setfunctions.submodular.Sum;
import org.getalp.similarity.local.SimilarityMeasure;

import java.util.ArrayList;
import java.util.List;

public class LegacyWindowedLesk implements Disambiguator {

    private int window;
    private SimilarityMeasure sim;
    private boolean fallbackFS;
    private boolean minimize;
    private double deltaThreshold;

    public LegacyWindowedLesk(int window, SimilarityMeasure sim, boolean fallbackFS, boolean minimize) {
        this.window = window;
        this.sim = sim;
        this.fallbackFS = fallbackFS;
        this.minimize = minimize;
        deltaThreshold = 0.005d;
    }

    @Override
    public Configuration disambiguate(Document document) {
        return disambiguate(document, null);
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {
        boolean progressChecked = false;
        if (c == null) {
            c = new Configuration(document);
        }

        for (int i = 0; i < document.getLexicalEntries().size(); i++) {
            LexicalEntry currentEntry = document.getLexicalEntries().get(i);
            String progress = String.format("\tDisambiguating: %.2f%%", ((double) i / (double) document.getLexicalEntries().size()) * 100d);
            System.err.print(progress);
            int start = (i <= window) ? 0 : i - window;
            int end = (i + window < document.getLexicalEntries().size()) ? i + window : document.getLexicalEntries().size();

            List<SubConfiguration> combinations = getCombinations(start, new SubConfiguration(document, start, end), document);
            int selected = -1;
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

            Function f = new Sum(1);
            for (int cmb = 0; cmb < combinations.size(); cmb++) {
                String wordProgress = String.format("%.2f%%", (100d * ((double) cmb / (double) combinations.size())));
                System.err.print(progress + " ==> " + currentEntry.getLemma() + "#" + currentEntry.getPos() + " [" + cmb + "/" + combinations.size() + " | " + wordProgress + "]\r");
                SubConfiguration sc = combinations.get(cmb);
                SenseCombinationInput sci = new SenseCombinationInput(sc, document, i - sc.getStart(), sim);
                double score = f.F(sci);
                //System.err.println(score);
                if (score <= minValue) {
                    prevMinValue = minValue;
                    minValue = score;
                    minIndex = cmb;
                } else if (score >= maxValue) {
                    prevMaxValue = maxValue;
                    maxValue = score;
                    maxIndex = cmb;
                }
            }
            double range = maxValue - minValue;
            double delta = range * deltaThreshold;

            if (((minimize && (minIndex == -1 || Math.abs(prevMinValue - minValue) < deltaThreshold)) ||
                    (!minimize && (maxIndex != 0 || Math.abs(prevMaxValue - maxValue) < deltaThreshold)))) {
                selected = -1;
            } else if (minimize) {
                selected = combinations.get(minIndex).getAssignment(i - combinations.get(minIndex).getStart());
            } else {
                selected = combinations.get(maxIndex).getAssignment(i - combinations.get(maxIndex).getStart());
            }


            if (fallbackFS && selected == -1) selected = 0;
            c.setSense(i, selected);
            c.setConfidence(i, 1d);
        }
        return c;
    }

    @Override
    public void release() {

    }

    public List<SubConfiguration> getCombinations(int currentIndex, SubConfiguration s, Document d) {
        List<SubConfiguration> combinations = new ArrayList<SubConfiguration>();

        if (currentIndex < s.size()) {
            try {
                for (int i = 0; i < d.getSenses(s, currentIndex).size(); i++) {
                    SubConfiguration newSub = new SubConfiguration(s);
                    newSub.setSense(currentIndex, i);
                    combinations.addAll(getCombinations(currentIndex + 1, newSub, d));
                }
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        } else {
            combinations.add(s);
        }
        return combinations;
    }
}
