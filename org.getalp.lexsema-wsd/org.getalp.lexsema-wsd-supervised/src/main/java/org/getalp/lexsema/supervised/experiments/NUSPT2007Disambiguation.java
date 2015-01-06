package org.getalp.lexsema.supervised.experiments;


import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.SemCorTextLoader;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.WekaDisambiguator;
import org.getalp.lexsema.supervised.features.*;
import org.getalp.lexsema.supervised.features.extractors.AggregateLocalTextFeatureExtractor;
import org.getalp.lexsema.supervised.features.extractors.AlignedContextFeatureExtractor;
import org.getalp.lexsema.supervised.features.extractors.LocalCollocationFeatureExtractor;
import org.getalp.lexsema.supervised.features.extractors.PosFeatureExtractor;
import org.getalp.lexsema.supervised.weka.BFTreeSetUp;
import org.getalp.lexsema.supervised.weka.BayesianLogisticRegressionSetUp;
import org.getalp.lexsema.supervised.weka.LoadClassifierModelSetUp;
import org.getalp.lexsema.supervised.weka.NaiveBayesSetUp;
import org.getalp.lexsema.supervised.weka.RBFNetworkSetUp;
import org.getalp.lexsema.supervised.weka.RandomForestSetUp;
import org.getalp.lexsema.supervised.weka.SVMSetUp;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;

import weka.classifiers.trees.RandomForest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class NUSPT2007Disambiguation {
    public static void main(String[] args) throws IOException {
        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml")
                .loadNonInstances(false);
        WordnetLoader lrloader = new WordnetLoader("../data/wordnet/2.1/dict")
                .setShuffle(false).setHasExtendedSignature(true);
        TextLoader semCor = new SemCorTextLoader("../data/semcor3.0/semcor_full.xml");

        semCor.load();
        WindowLoader wloader = new DocumentCollectionWindowLoader(semCor);
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
        LocalCollocationFeatureExtractor lcfe = new LocalCollocationFeatureExtractor(contextWindows);
        PosFeatureExtractor pfe = new PosFeatureExtractor(3,3);
        AlignedContextFeatureExtractor acfe = new AlignedContextFeatureExtractor(wloader);

        AggregateLocalTextFeatureExtractor altfe = new AggregateLocalTextFeatureExtractor();
        altfe.addExtractor(lcfe);
        altfe.addExtractor(pfe);
        altfe.addExtractor(acfe);

        TrainingDataExtractor trainingDataExtractor = new SemCorTrainingDataExtractor(altfe);
        trainingDataExtractor.extract(semCor);
        
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new SVMSetUp(), altfe, 16);
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new BFTreeSetUp(), altfe, 16);
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new BayesianLogisticRegressionSetUp(), altfe, 16);
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new RBFNetworkSetUp(), altfe, 16);
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new RandomForestSetUp(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]),Integer.parseInt(args[3])), altfe, Integer.parseInt(args[4]));
        Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new NaiveBayesSetUp(Boolean.parseBoolean(args[0]), Boolean.parseBoolean(args[1])), altfe, Integer.parseInt(args[2]), trainingDataExtractor);
        
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

            Configuration c = disambiguator.disambiguate(d);
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d, c.getAssignments());
            System.err.println("done!");
        }
        disambiguator.release();
    }
}
