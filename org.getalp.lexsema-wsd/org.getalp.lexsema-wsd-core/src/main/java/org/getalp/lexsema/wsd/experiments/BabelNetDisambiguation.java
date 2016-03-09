package org.getalp.lexsema.wsd.experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.lesk.*;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.*;
import org.getalp.lexsema.wsd.score.*;
import org.getalp.lexsema.io.resource.babelnet.*;

public class BabelNetDisambiguation
{
    public static void main(String[] args) throws Exception
    {
	    LRLoader lrloader = new BabelNetAPILoader("../data/babelnet/2.5.1/babelnet.properties", Language.ENGLISH);
	
	    CorpusLoader dl = new Semeval2007CorpusLoader(new FileInputStream("../data/senseval2007_task7/test/eng-coarse-all-words.xml"));
	    dl.load();
	    for (Document d : dl) lrloader.loadSenses(d);
	
	    ConfigurationScorer scorer = new ConfigurationScorerWithCache(new VectorizedLeskSimilarity());
	        
	    SemEval2007Task7PerfectConfigurationScorer perfectScorer = new SemEval2007Task7PerfectConfigurationScorer();
	
	    int iterations = 100000;
	    double minLevyLocation = 1;
	    double maxLevyLocation = 5;
	    double minLevyScale = 0.5;
	    double maxLevyScale = 1.5;
	
	    MultiThreadCuckooSearch cuckooDisambiguator = new MultiThreadCuckooSearch(iterations, minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, scorer, true);               

        for (Document d : dl)
        {
            Configuration c = cuckooDisambiguator.disambiguate(d);
            double tmp_score = perfectScorer.computeScore(d, c);
        }
        
        cuckooDisambiguator.release();
        
    }
}
