package org.getalp.lexsema.wsd.experiments;


@SuppressWarnings("all")
public class AdaptiveSimulatedAnnealingDisambiguation {
    /*
    public AdaptiveSimulatedAnnealingDisambiguation() {
    }

    public static void main(String[] args) {
        //VisualVMTools.delayUntilReturn();
        long startTime = System.currentTimeMillis();
        CorpusLoader dl = new Semeval2007CorpusLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml").loadNonInstances(true);
        LRLoader lrloader = new WordnetLoader2("../data/wordnet/2.1/dict")
			.extendedSignature(true)
			.filterStopWords(false)
			.stemming(true)
			.loadDefinitions(true);
        
        CorpusLoader semCor = new SemCorCorpusLoader("../data/semcor3.0/semcor_full.xml");
		semCor.load();
        
        SemCorDefinitionExpender definitionExpender=null;
		definitionExpender=new SemCorDefinitionExpender(semCor, 2);
		
		DSODefinitionExpender contexteDSO=null;
		//contexteDSO=new DSODefinitionExpender(10);
        
        //LRLoader lrloader = new WordnetLoader2("../data/wordnet/2.1/dict")
        //        .extendedSignature(true).loadDefinitions(true).shuffle(false);
        SimilarityMeasure sim;

        //sim = new IndexedOverlapSimilarity().normalize(false);
        sim = new TverskiIndexSimilarityMeasureBuilder().distance(new ScaledLevenstein()).computeRatio(true).alpha(1d).beta(0.5d).gamma(0.5d).fuzzyMatching(false).quadraticWeighting(false).extendedLesk(false).randomInit(false).regularizeOverlapInput(false).optimizeOverlapInput(false).regularizeRelations(false).optimizeRelations(false).build();

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

        //ConfigurationScorer configurationScorer = new MatrixConfigurationScorer(sim, new NormalizationFilter(NormalizationFilter.NormalizationType.UNIT_NORM), new SumMatrixScorer(), threads);
        ConfigurationScorer configurationScorer = new TverskyConfigurationScorer(sim, threads);
        //ConfigurationScorer configurationScorer = new ACSimilarityConfigurationScorer(sim);

        Disambiguator sl_full = new SimulatedAnnealing(accptProb, cR, (int) convThresh, iterations, configurationScorer, 1000);

        System.err.println("Loading texts");
        dl.load();

        for (Document d : dl) {
            System.err.println("Starting document " + d.getId());
            System.err.println("\tLoading senses...");
            lrloader.loadSenses(d, definitionExpender, 1, contexteDSO);

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
*/
}
