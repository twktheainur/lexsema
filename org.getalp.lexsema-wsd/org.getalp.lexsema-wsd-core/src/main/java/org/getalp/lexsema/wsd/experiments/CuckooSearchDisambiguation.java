package org.getalp.lexsema.wsd.experiments;

import java.io.File;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.IndexedOverlapSimilarity;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.CuckooSearch;
import org.getalp.lexsema.wsd.method.Disambiguator;

public class CuckooSearchDisambiguation
{
    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();

        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words-t1.xml")
                .loadNonInstances(true);

        LRLoader lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));

        SimilarityMeasure sim = new IndexedOverlapSimilarity();
/*
        LRLoader lrloader = new WordnetLoader("../data/wordnet/2.1/dict")
                .extendedSignature(true)
                .shuffle(false)
                .setUsesStopWords(false)
                .setStemming(false)
                .loadDefinitions(true);
*/
/*
        SimilarityMeasure sim = new TverskiIndexSimilarityMeasureBuilder()
                .distance(new ScaledLevenstein())
                .computeRatio(false)
                .alpha(1)
                .beta(0)
                .gamma(0)
                .build();
*/
        Disambiguator cuckooDisambiguator = new CuckooSearch(sim);

        System.err.println("Loading texts");
        dl.load();

        for (Document d : dl)
        {    
            System.err.println("Starting document " + d.getId());
            
            System.err.println("\tLoading senses...");
            lrloader.loadSenses(d);

            System.err.println("Disambiguating...");
            Configuration c = cuckooDisambiguator.disambiguate(d);
            
            System.err.println("\n\tWriting results...");
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            sw.write(d, c.getAssignments());
            
            System.err.println("done!");
        }
        
        cuckooDisambiguator.release();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Cuckoo Search Algorithm is : ");
        System.out.println((endTime - startTime) + " ms.");
        System.out.println(((endTime - startTime) / 1000l) + " s.");
        System.out.println(((endTime - startTime) / 60000l) + " m.");
    }
}
