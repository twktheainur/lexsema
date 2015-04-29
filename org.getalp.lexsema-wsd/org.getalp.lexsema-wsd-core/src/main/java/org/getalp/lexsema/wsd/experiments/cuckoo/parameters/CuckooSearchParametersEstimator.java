package org.getalp.lexsema.wsd.experiments.cuckoo.parameters;

import java.io.File;
import java.io.PrintWriter;

import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSearch;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;

public class CuckooSearchParametersEstimator
{
    public static void main(String[] args) throws Exception
    {
        int iterations = 1000;
        double levyScale = 0.5;
        int nestsNumber = 20;
        int destroyedNests = 5;
        int insideIterations = 1000;
        
        if (args.length >= 1) iterations = Integer.valueOf(args[0]);
        if (args.length >= 2) levyScale = Double.valueOf(args[1]);
        if (args.length >= 3) nestsNumber = Integer.valueOf(args[2]);
        if (args.length >= 4) destroyedNests = Integer.valueOf(args[3]);
        if (args.length >= 5) insideIterations = Integer.valueOf(args[4]);
        
        System.out.println("Parameters value : " +
                           "<iterations = " + iterations + "> " +
                           "<levy scale = " + levyScale + "> " +
                           "<nests number = " + nestsNumber + "> " +
                           "<destroyed nests = " + destroyedNests + "> " +
                           "<inside iterations = " + insideIterations + "> ");

        long startTime = System.currentTimeMillis();

        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words-t1.xml");
        dl.loadNonInstances(false);
        dl.load();
        
        LRLoader lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));
        for (Document d : dl) lrloader.loadSenses(d);
        
        ConfigurationScorer configScorer = new SemEval2007Task7PerfectConfigurationScorer();
        
        CuckooParametersScorer scorer = new CuckooParametersScorer(configScorer, dl, 5, insideIterations); 
        
        CuckooParametersFactory configFactory = new CuckooParametersFactory();
        
        CuckooSearch cuckoo = new CuckooSearch(iterations, levyScale, nestsNumber, destroyedNests, 
                                                 scorer, configFactory);
        
        CuckooSolution params = cuckoo.run();
        
        PrintWriter writer = new PrintWriter("cuckoo_parameters.txt");
        writer.println(params.toString());
        writer.close();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Cuckoo Search Parameters Algorithm is : ");
        System.out.println((endTime - startTime) + " ms.");
        System.out.println(((endTime - startTime) / 1000l) + " s.");
        System.out.println(((endTime - startTime) / 60000l) + " m.");
    }
}
