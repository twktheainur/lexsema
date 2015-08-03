package org.getalp.lexsema.wsd.experiments;

import com.wcohen.ss.ScaledLevenstein;

import edu.mit.jwi.Dictionary;

import org.getalp.lexsema.io.dictionary.DictionaryWriter;
import org.getalp.lexsema.io.dictionary.DocumentDictionaryWriter;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.evaluation.Semeval2007GoldStandard;
import org.getalp.lexsema.wsd.evaluation.StandardEvaluation;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.sequencial.WindowedLesk;
import org.getalp.lexsema.wsd.method.sequencial.parameters.SimplifiedLeskParameters;
import org.getalp.lexsema.wsd.method.sequencial.parameters.WindowedLeskParameters;

import java.io.File;

@SuppressWarnings("all")
public class CombinedDisambiguation {
    public CombinedDisambiguation() {
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        CorpusLoader dl = new Semeval2007CorpusLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml").loadNonInstances(true);
        LRLoader lrloader = new WordnetLoader(new Dictionary(new File("../data/wordnet/2.1/dict")))
		.extendedSignature(true)
		.filterStopWords(false)
		.stemming(true)
		.loadDefinitions(true);
        SimilarityMeasure sim_lr_hp;
        SimilarityMeasure sim_full;

        //sim_lr_hp = new TverskiIndex(new ScaledLevenstein(),false, 1d, 0d, 0d, true, true,false ,true);
        //sim_lr_hp = new TverskiIndexSimilarityMeasureBuilder().distance(new ScaledLevenstein()).computeRatio(true).alpha(1d).beta(0.5d).gamma(0.5d).fuzzyMatching(true).quadraticWeighting(false).extendedLesk(false).randomInit(false).regularizeOverlapInput(false).optimizeOverlapInput(false).regularizeRelations(false).optimizeRelations(false).build();

		SimplifiedLeskParameters slp = new SimplifiedLeskParameters()
				.setAllowTies(false)
                .setDeltaThreshold(0.000001d)
				.setIncludeTarget(false)
				.setOnlyUniqueWords(false)
				.setFallbackFS(false)
				.setMinimize(false);
				//.filterStopWords(true)
				//.stemming(true);
        //Disambiguator sl = new SimplifiedLesk(100, sim_lr_hp, slp, 4);  
        /*
        DSOThesaurus contexteDSO=null;
		contexteDSO=new DSOThesaurus(3);
*/
        WindowedLeskParameters wlp = new WindowedLeskParameters().setFallbackFS(false).setMinimize(false);
        sim_full = new TverskiIndexSimilarityMeasureBuilder().distance(new ScaledLevenstein()).computeRatio(true).alpha(1d).beta(0.5d).gamma(0.5d).fuzzyMatching(false).quadraticWeighting(false).extendedLesk(false).randomInit(false).regularizeOverlapInput(false).optimizeOverlapInput(false).regularizeRelations(false).optimizeRelations(false).build();
        Disambiguator sl_full = new WindowedLesk(2, sim_full, wlp, Runtime.getRuntime().availableProcessors());

        //Disambiguator sl = new LegacySimplifiedLesk(10,sim_lr_hp,);
        //WindowedLeskParameters wlp = new WindowedLeskParameters(false,false);
        //Disambiguator sl = new WindowedLesk(6, sim_lr_hp, wlp, 4);
        System.err.println("Loading texts");
        dl.load();

        Semeval2007GoldStandard goldStandard = new Semeval2007GoldStandard();
        StandardEvaluation evaluation = new StandardEvaluation();

        for (Document d : dl) {
            System.err.println("Starting document " + d.getId());
            System.err.println("\tLoading senses...");
            //lrloader.loadSenses(d, null, 0, contexteDSO);
            lrloader.loadSenses(d);
            //System.err.println("\tDisambiguating... ");
            //System.err.println("Applying low recall high precision simplified lesk...");
            //Configuration c = sl.disambiguate(d);


            System.err.println("Disambiguating...");
            //sl_full.disambiguate(d, c);
            Configuration c = sl_full.disambiguate(d);
            System.err.println(evaluation.evaluate(goldStandard, c).getPrecision());
            //SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            //System.err.println("\n\tWriting results...");
            //sw.write(d, c.getAssignments());
            System.err.println("done!");
        }
        DictionaryWriter writer = new DocumentDictionaryWriter(dl);
        writer.writeDictionary(new File("dictTest2.xml"));
        //sl.release();
        sl_full.release();
        long endTime = System.currentTimeMillis();
        System.out.println("Total elapsed time in execution of CombinedDisambiguation is : " + (endTime - startTime) + " ms.");

    }
}
