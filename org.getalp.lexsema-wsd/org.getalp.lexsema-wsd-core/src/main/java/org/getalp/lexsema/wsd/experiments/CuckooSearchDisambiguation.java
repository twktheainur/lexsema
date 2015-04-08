package org.getalp.lexsema.wsd.experiments;

import java.io.File;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.CuckooSearch;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;

public class CuckooSearchDisambiguation
{
    public static void main(String[] args)
    {
        int iterations = 1000;
        double levyScale = 0.5;
        int nestsNumber = 20;
        int destroyedNests = 5;
        
        if (args.length >= 1) iterations = Integer.valueOf(args[0]);
        if (args.length >= 2) levyScale = Double.valueOf(args[1]);
        if (args.length >= 3) nestsNumber = Integer.valueOf(args[2]);
        if (args.length >= 4) destroyedNests = Integer.valueOf(args[3]);
        
        System.out.println("Parameters value : " +
                           "<iterations = " + iterations + "> " +
                           "<levy scale = " + levyScale + "> " +
                           "<nests number = " + nestsNumber + "> " +
                           "<destroyed nests = " + destroyedNests + "> ");
        
        long startTime = System.currentTimeMillis();

        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml")
                .loadNonInstances(false);

        LRLoader lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));
        
        ConfigurationScorer scorer = new SemEval2007Task7PerfectConfigurationScorer();

        Disambiguator cuckooDisambiguator = new CuckooSearch(iterations, levyScale, nestsNumber, destroyedNests, scorer);

        System.out.println("Loading texts...");
        dl.load();

        for (Document d : dl)
        {
            System.out.println("Starting document " + d.getId());
            
            System.out.println("Loading senses...");
            lrloader.loadSenses(d);

            System.out.println("Disambiguating...");
            Configuration c = cuckooDisambiguator.disambiguate(d);

            System.out.println("Writing results...");
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            sw.write(d, c.getAssignments());
            
            System.out.println("Done!");
        }
        
        cuckooDisambiguator.release();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Cuckoo Search Algorithm is : ");
        System.out.println((endTime - startTime) + " ms.");
        System.out.println(((endTime - startTime) / 1000l) + " s.");
        System.out.println(((endTime - startTime) / 60000l) + " m.");
    }
}
