package org.getalp.lexsema.wsd.experiments;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.IndexedOverlapSimilarity;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.util.VisualVMTools;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.SimulatedAnnealing;

import java.io.File;

@SuppressWarnings("all")
public class AdaptiveSimulatedAnnealingDisambiguation {
    public AdaptiveSimulatedAnnealingDisambiguation() {
    }

    public static void main(String[] args) {
        VisualVMTools.delayUntilReturn();
        long startTime = System.currentTimeMillis();
        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words-t1.xml");
        LRLoader lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));
        SimilarityMeasure sim;

        sim = new IndexedOverlapSimilarity();

        if (args.length < 3) {
            System.err.println("Usage: aSAD [P0] [cR] [cT] (threads)");
            System.exit(0);
        }
        double accptProb = Double.parseDouble(args[0]);
        double cR = Double.parseDouble(args[1]);
        double convThresh = Double.parseDouble(args[2]);
        int threads = 1;
        if (args.length == 3) {
            threads = Integer.parseInt(args[3]);
        } else {
            threads = Runtime.getRuntime().availableProcessors();
        }

        Disambiguator sl_full = new SimulatedAnnealing(accptProb, cR, (int) convThresh, threads, sim);

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
