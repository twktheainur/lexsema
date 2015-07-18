package org.getalp.lexsema.wsd.experiments;

import edu.mit.jwi.Dictionary;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureMatrixImplBuilder;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.MatrixBatAlgorithm;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.MatrixTverskiConfigurationScorer;
import org.getalp.ml.matrix.score.SumMatrixScorer;

import java.io.File;

public class MatrixBatAlgorithmDisambiguation {

	public static void main(String[] args) {
		
        long startTime = System.currentTimeMillis();
        
        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml")
        		.loadNonInstances(true);

        LRLoader lrloader = new WordnetLoader(new Dictionary(new File("../data/wordnet/2.1/dict")))
                .extendedSignature(true)
                .shuffle(false)
                .filterStopWords(false)
                .stemming(false)
                .loadDefinitions(true);

        SimilarityMeasure sim = new TverskiIndexSimilarityMeasureMatrixImplBuilder()
                .computeRatio(true)
                .alpha(1d)
                .beta(0.5d)
                .gamma(0.5d)
                .fuzzyMatching(false)
                //.filter(new NMFKLMatrixFactorizationFilter(-1))
                .matrixScorer(new SumMatrixScorer())
                .build();

        ConfigurationScorer configurationScorer =
                new MatrixTverskiConfigurationScorer(sim,null
                        /*new NMFKLMatrixFactorizationFilter(-1)*/,
                        new SumMatrixScorer(), Runtime.getRuntime().availableProcessors());
        Disambiguator batDisambiguator = new MatrixBatAlgorithm(configurationScorer);

//        VisualVMTools.delayUntilReturn();

        System.err.println("Loading texts");
        dl.load();

        for (Document d : dl) {
        	
            System.err.println("Starting document " + d.getId());
            
            System.err.println("\tLoading senses...");
            lrloader.loadSenses(d);

            System.err.println("Disambiguating...");
            Configuration c = batDisambiguator.disambiguate(d);
            
            System.err.println("\n\tWriting results...");
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            sw.write(d, c.getAssignments());
            
            System.err.println("done!");
        }
        
        batDisambiguator.release();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Bat Algorithm is : ");
        System.out.println((endTime - startTime) + " ms.");
	}
}
