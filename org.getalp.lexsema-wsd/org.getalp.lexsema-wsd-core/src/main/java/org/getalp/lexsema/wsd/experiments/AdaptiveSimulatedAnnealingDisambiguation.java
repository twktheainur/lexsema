package org.getalp.lexsema.wsd.experiments;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.util.VisualVMTools;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.AdaptiveSimulatedAnnealing;
import org.getalp.lexsema.wsd.method.Disambiguator;

@SuppressWarnings("all")
public class AdaptiveSimulatedAnnealingDisambiguation {
    public AdaptiveSimulatedAnnealingDisambiguation() {
    }

    public static void main(String[] args) {
        VisualVMTools.delayUntilReturn();
        long startTime = System.currentTimeMillis();
        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml").loadNonInstances(true);
        LRLoader lrloader = new WordnetLoader("../data/wordnet/2.1/dict")
                .extendedSignature(true)
                .shuffle(false)
                .setUsesStopWords(false)
                .setStemming(true)
                .loadDefinitions(true);
        SimilarityMeasure sim;

        sim = new TverskiIndexSimilarityMeasureBuilder().distance(new ScaledLevenstein())
                .computeRatio(true)
                .alpha(1d)
                .beta(0.5d)
                .gamma(0.5d)
                .fuzzyMatching(false)
                .quadraticWeighting(false)
                .extendedLesk(false).randomInit(false)
                .regularizeOverlapInput(false).optimizeOverlapInput(false)
                .regularizeRelations(false).optimizeRelations(false)
                .build();

        Disambiguator sl_full = new AdaptiveSimulatedAnnealing(0.4, 3, 3, 5, 4, sim);

        System.err.println("Loading texts");
        dl.load();

        for (Document d : dl) {
            System.err.println("Starting document " + d.getId());
            System.err.println("\tLoading senses...");
            lrloader.loadSenses(d);

            System.err.println("Disambiguating...");
            Configuration c = sl_full.disambiguate(d);
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d, c.getAssignments());
            System.err.println("done!");
        }
        //sl.release();
        sl_full.release();
        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Adapted Simulated Annealing is : " + (endTime - startTime) + " ms.");

    }
}
