package org.getalp.lexsema.supervised.experiments;


import edu.mit.jwi.Dictionary;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.WordnetGlossTagTextLoader;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.DSOCorpusLoader;
import org.getalp.lexsema.io.document.loader.SemCorCorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.supervised.WekaDisambiguator;
import org.getalp.lexsema.supervised.features.*;
import org.getalp.lexsema.supervised.features.extractors.*;
import org.getalp.lexsema.supervised.weka.RandomForestSetUp;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.FirstSenseDisambiguator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class NUSPT2007Disambiguation {
    public static void main(String[] args) throws IOException {
        CorpusLoader dl = new Semeval2007CorpusLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml")
                .loadNonInstances(false);
        LRLoader lrloader = new WordnetLoader(new Dictionary(new File("../data/wordnet/2.1/dict")));
              //  .shuffle(false).extendedSignature(true);

        boolean useSemCor = false;
        boolean useDso = false;
        boolean useWNG = false;

        CorpusLoader dso = null;
        CorpusLoader semCor = null;
        CorpusLoader wng = null;


        if (useSemCor)
            semCor = new SemCorCorpusLoader("../data/semcor3.0/semcor_full.xml");

        if (useDso)
            dso = new DSOCorpusLoader("../data/dso","../data/wordnet/2.1/dict");

        if (useWNG)
            wng = new WordnetGlossTagTextLoader("../data/glosstag");

        if (useSemCor)
            semCor.load();

        if (useDso)
            dso.load();

        if(useWNG)
            wng.load();

        List<Text> taggedCorpora = new ArrayList<>();

        if (useSemCor)
            for(Text t: semCor) {
                taggedCorpora.add(t);
            }

        if(useDso)
            for(Text t: dso) {
                taggedCorpora.add(t);
            }

        if(useWNG)
            for(Text t: wng) {
                taggedCorpora.add(t);
            }

        WindowLoader wloader = new DocumentCollectionWindowLoader(taggedCorpora);
        wloader.load();

        List<ContextWindow> contextWindows = new ArrayList<>();
        contextWindows.add(new ContextWindow(-1, -1));
        contextWindows.add(new ContextWindow(1, 1));
        contextWindows.add(new ContextWindow(-2, -2));
        contextWindows.add(new ContextWindow(2, 2));
        contextWindows.add(new ContextWindow(-2, -1));
        contextWindows.add(new ContextWindow(-1, 1));
        contextWindows.add(new ContextWindow(1, 2));
        contextWindows.add(new ContextWindow(-3, -1));
        contextWindows.add(new ContextWindow(-2, 1));
        contextWindows.add(new ContextWindow(-1, 2));
        contextWindows.add(new ContextWindow(1, 3));
        /*
        contextWindows.add(new ContextWindow(-5, 5));
        contextWindows.add(new ContextWindow(-4, 4));
        contextWindows.add(new ContextWindow(-3, 3));
        contextWindows.add(new ContextWindow(-2, 2));
        contextWindows.add(new ContextWindow(-1, 1));
        */
        LocalCollocationFeatureExtractor lcfe = new LocalCollocationFeatureExtractor(contextWindows,false);
        PosFeatureExtractor pfe = new PosFeatureExtractor(3, 3);
        LocalTextFeatureExtractor acfe = new LemmaFeatureExtractor(3, 3);

        AggregateLocalTextFeatureExtractor altfe = new AggregateLocalTextFeatureExtractor();
        altfe.addExtractor(lcfe);
        altfe.addExtractor(pfe);
        altfe.addExtractor(acfe);

        TrainingDataExtractor trainingDataExtractor = new SemCorTrainingDataExtractor(altfe);
        trainingDataExtractor.extract(taggedCorpora);

        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new SVMSetUp(), altfe, 4, trainingDataExtractor);
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new BFTreeSetUp(), altfe, 16);
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new BayesianLogisticRegressionSetUp(), altfe, 16);
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new RBFNetworkSetUp(), altfe, 16);
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new RandomForestSetUp(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]),Integer.parseInt(args[3])), altfe, Integer.parseInt(args[4]), trainingDataExtractor);
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new NaiveBayesSetUp(Boolean.parseBoolean(args[0]), Boolean.parseBoolean(args[1])), altfe, Integer.parseInt(args[2]), trainingDataExtractor);

        Disambiguator firstSenseDisambiguator = new FirstSenseDisambiguator();

        System.err.println("Loading texts");
        dl.load();
        int i = 0;
        if (args.length == 1) {
            i = Integer.valueOf(args[0]) - 1;
        }
        for (Document d : dl) {
            System.err.println("Starting document " + d.getId());
            System.err.println("\tLoading senses...");
            lrloader.loadSenses(d);

            //Configuration c = disambiguator.disambiguate(d);
            Configuration c = firstSenseDisambiguator.disambiguate(d);

            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d, c.getAssignments());
            System.err.println("done!");
        }
        //disambiguator.release();
        firstSenseDisambiguator.release();
    }
}
