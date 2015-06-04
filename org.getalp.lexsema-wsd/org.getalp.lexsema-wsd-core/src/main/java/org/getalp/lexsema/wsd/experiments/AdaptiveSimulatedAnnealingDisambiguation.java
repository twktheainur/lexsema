package org.getalp.lexsema.wsd.experiments;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.lesk.ACExtendedLeskSimilarity;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.SimulatedAnnealing;
import org.getalp.lexsema.wsd.score.ACSimilarityConfigurationScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.MatrixTverskiConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;
import org.getalp.ml.matrix.filters.NormalizationFilter;
import org.getalp.ml.matrix.score.SumMatrixScorer;

import java.io.File;

@SuppressWarnings("all")
public class AdaptiveSimulatedAnnealingDisambiguation {
    public AdaptiveSimulatedAnnealingDisambiguation() {
    }

    public static void main(String[] args) {
        //VisualVMTools.delayUntilReturn();
        long startTime = System.currentTimeMillis();
        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words-t1.xml");
        LRLoader lrloader = new WordnetLoader("../data/wordnet/2.1/dict")
		.extendedSignature(true)
		.setUsesStopWords(false)
		.setStemming(true)
		.loadDefinitions(true);
        //LRLoader lrloader = new WordnetLoader("../data/wordnet/2.1/dict")
        //        .extendedSignature(true).loadDefinitions(true).shuffle(false);
        SimilarityMeasure sim;

        //sim = new IndexedOverlapSimilarity().normalize(false);
        //sim = new ACExtendedLeskSimilarity();
        sim = new ACExtendedLeskSimilarity();

        if (args.length < 4) {
            System.err.println("Usage: aSAD [P0] [cR] [cT] [It] (threads)");
            System.exit(0);
        }
        double accptProb = Double.parseDouble(args[0]);
        double cR = Double.parseDouble(args[1]);
        double convThresh = Double.parseDouble(args[2]);
        int iterations = Integer.parseInt(args[3]);
        int threads = 1;
        if (args.length > 4) {
            threads = Integer.parseInt(args[4]);
        } else {
            threads = Runtime.getRuntime().availableProcessors();
        }

        //ConfigurationScorer configurationScorer = new MatrixTverskiConfigurationScorer(sim, new NormalizationFilter(NormalizationFilter.NormalizationType.UNIT_NORM), new SumMatrixScorer(), threads);
        ConfigurationScorer configurationScorer = new TverskyConfigurationScorer(sim, threads);
        //ConfigurationScorer configurationScorer = new ACSimilarityConfigurationScorer(sim);

        Disambiguator sl_full = new SimulatedAnnealing(accptProb, cR, (int) convThresh, iterations, configurationScorer, 1000);

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
