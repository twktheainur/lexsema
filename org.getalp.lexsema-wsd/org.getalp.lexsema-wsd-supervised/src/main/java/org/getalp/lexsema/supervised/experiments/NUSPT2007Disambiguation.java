package org.getalp.lexsema.supervised.experiments;


import edu.mit.jwi.Dictionary;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.loader.*;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.supervised.WekaDisambiguator;
import org.getalp.lexsema.supervised.features.*;
import org.getalp.lexsema.supervised.features.extractors.*;
import org.getalp.lexsema.supervised.weka.SVMSetUp;
import org.getalp.lexsema.supervised.weka.NaiveBayesSetUp;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.FirstSenseDisambiguator;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public final class NUSPT2007Disambiguation {

    public static void main(String[] args) throws IOException {

        boolean useSemCor = true;

        boolean useDso = true;
        boolean useWNG = true;
        boolean useGMB = false;
        boolean backoff = true;

        boolean toDisambiguate[] = {true, true, true, true, true};

        classicDisamb(args, toDisambiguate, useSemCor, useDso, useWNG, useGMB, backoff);

    }

    public static void incrementalDisamb(String[] args, boolean toDisambiguate[], boolean useSemCor, boolean useDso, boolean useWNG, boolean useGMB, boolean backoff) throws IOException {

        CorpusLoader dl = new Semeval2007CorpusLoader(new FileInputStream("../data/senseval2007_task7/test/eng-coarse-all-words.xml"))
                .loadNonInstances(false);
        LRLoader lrloader = new WordnetLoader(new Dictionary(new URL("../data/wordnet/2.1/dict")));//.shuffle(true).extendedSignature(true);

        Configuration[] configs = new Configuration[5];

        CorpusLoader dso = null;
        CorpusLoader semCor = null;
        CorpusLoader wng = null;
        CorpusLoader gmb = null;


        List<Text> taggedCorpora = new ArrayList<>();

        Disambiguator firstSenseDisambiguator = new FirstSenseDisambiguator();
        //GoldStandard goldStandard = new Semeval2007GoldStandard();
        // Evaluation standardEvaluation = new StandardEvaluation();

        System.err.println("Loading texts");
        dl.load();
        int i = 0;
        if (args.length == 1) {
            i = Integer.valueOf(args[0]) - 1;
        }
        System.err.println("Texts loaded");


        WindowLoader wloader = new DocumentCollectionWindowLoader(taggedCorpora);
        wloader.load();

        List<ContextWindow> contextWindows = new ArrayList<>();
        contextWindows.add(new ContextWindowImpl(-1, -1));
        contextWindows.add(new ContextWindowImpl(1, 1));
        contextWindows.add(new ContextWindowImpl(-2, -2));
        contextWindows.add(new ContextWindowImpl(2, 2));
        contextWindows.add(new ContextWindowImpl(-2, -1));
        contextWindows.add(new ContextWindowImpl(-1, 1));
        contextWindows.add(new ContextWindowImpl(1, 2));
        contextWindows.add(new ContextWindowImpl(-3, -1));
        contextWindows.add(new ContextWindowImpl(-2, 1));
        contextWindows.add(new ContextWindowImpl(-1, 2));
        contextWindows.add(new ContextWindowImpl(1, 3));

        System.err.println("Feature extraction");

        LocalCollocationFeatureExtractor lcfe = new LocalCollocationFeatureExtractor(contextWindows, false);
        PosFeatureExtractor pfe = new PosFeatureExtractor(3, 3);
        LocalTextFeatureExtractor acfe = new LemmaFeatureExtractor(3, 3);


        AggregateLocalTextFeatureExtractor altfe = new AggregateLocalTextFeatureExtractor();
        altfe.addExtractor(lcfe);
        altfe.addExtractor(pfe);
        altfe.addExtractor(acfe);

        System.err.println("Feature extraction done...");

        if (useDso) {
            dso = new DSOCorpusLoader("../data/dso", "../data/wordnet/2.1/dict", false);
            dso.load();
            for (Text t : dso) {
                taggedCorpora.add(t);
            }

            TrainingDataExtractor trainingDataExtractor = new SemCorTrainingDataExtractor(altfe);
            trainingDataExtractor.extract(taggedCorpora);

            System.err.println("Feature extraction done");

            Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new SVMSetUp(), altfe, 4, trainingDataExtractor);
            //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new RandomForestSetUp(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])), altfe, Integer.parseInt(args[4]), trainingDataExtractor);


            System.err.println("Desambiguation with DSO");

            int numconfig = 0;
            for (Document d : dl) {

                if (toDisambiguate[numconfig]) {
                    System.err.println("Starting document " + d.getId());
                    System.err.println("\tLoading senses...");
                    if (!d.isAlreadyLoaded())
                        lrloader.loadSenses(d);

                    configs[numconfig] = disambiguator.disambiguate(d);
                    //     System.err.println(standardEvaluation.evaluate(goldStandard, config));

                    SemevalWriter sw = new SemevalWriter(d.getId() + "-DSO");
                    System.err.println("\n\tWriting results...");
                    sw.write(d, configs[numconfig].getAssignments());
                    System.err.println("done!");

                }
                numconfig++;
            }

            disambiguator.release();
            System.err.println("Desambiguation with DSO done");
        }

        if (useGMB) {

            gmb = new GMBCorpusLoader("../data/GMB/gmb-2.2.0/", new Dictionary(new URL("../data/wordnet/2.1/dict")));
            gmb.load();
            for (Text t : gmb) {
                taggedCorpora.add(t);
            }

            TrainingDataExtractor trainingDataExtractor = new SemCorTrainingDataExtractor(altfe);
            trainingDataExtractor.extract(taggedCorpora);

            System.err.println("Feature extraction done");

            Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new SVMSetUp(), altfe, 4, trainingDataExtractor);
            //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new RandomForestSetUp(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])), altfe, Integer.parseInt(args[4]), trainingDataExtractor);


            System.err.println("Desambiguation with Groningen Meaning Bank");

            int numconfig = 0;
            for (Document d : dl) {

                if (toDisambiguate[numconfig]) {
                    System.err.println("Starting document " + d.getId());
                    System.err.println("\tLoading senses...");
                    if (!d.isAlreadyLoaded())
                        lrloader.loadSenses(d);

                    configs[numconfig] = disambiguator.disambiguate(d);
                    //     System.err.println(standardEvaluation.evaluate(goldStandard, config));

                    SemevalWriter sw = new SemevalWriter(d.getId() + "-gmb");
                    System.err.println("\n\tWriting results...");
                    sw.write(d, configs[numconfig].getAssignments());
                    System.err.println("done!");

                }
                numconfig++;
            }

            disambiguator.release();
            System.err.println("Desambiguation with Groningen Meaning Bank done");

        }

        if (useSemCor) {
            semCor = new SemCorCorpusLoader("../data/semcor3.0/semcor_full.xml");
            semCor.load();
            for (Text t : semCor) {
                taggedCorpora.add(t);
            }

            TrainingDataExtractor trainingDataExtractor = new SemCorTrainingDataExtractor(altfe);
            trainingDataExtractor.extract(taggedCorpora);

            System.err.println("Feature extraction done");

            Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new SVMSetUp(), altfe, 4, trainingDataExtractor);
            //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new RandomForestSetUp(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])), altfe, Integer.parseInt(args[4]), trainingDataExtractor);


            System.err.println("Desambiguation with SemCor");

            int numconfig = 0;
            for (Document d : dl) {

                if (toDisambiguate[numconfig]) {
                    System.err.println("Starting document " + d.getId());
                    System.err.println("\tLoading senses...");
                    if (!d.isAlreadyLoaded())
                        lrloader.loadSenses(d);

                    configs[numconfig] = disambiguator.disambiguate(d);
                    //     System.err.println(standardEvaluation.evaluate(goldStandard, config));

                    SemevalWriter sw = new SemevalWriter(d.getId() + "-semcor");
                    System.err.println("\n\tWriting results...");
                    sw.write(d, configs[numconfig].getAssignments());
                    System.err.println("done!");

                }
                numconfig++;
            }

            disambiguator.release();
            System.err.println("Desambiguation with SemCor done");


        }

        if (useWNG) {
            wng = new WordnetGlossTagCorpusLoader("../data/glosstag");
            wng.load();
            for (Text t : wng) {
                taggedCorpora.add(t);
            }

            TrainingDataExtractor trainingDataExtractor = new SemCorTrainingDataExtractor(altfe);
            trainingDataExtractor.extract(taggedCorpora);

            System.err.println("Feature extraction done");

            Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new SVMSetUp(), altfe, 4, trainingDataExtractor);
            //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new RandomForestSetUp(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])), altfe, Integer.parseInt(args[4]), trainingDataExtractor);


            System.err.println("Desambiguation with WordNet Glosses");

            int numconfig = 0;
            for (Document d : dl) {

                if (toDisambiguate[numconfig]) {
                    System.err.println("Starting document " + d.getId());
                    System.err.println("\tLoading senses...");
                    if (!d.isAlreadyLoaded())
                        lrloader.loadSenses(d);

                    configs[numconfig] = disambiguator.disambiguate(d);
                    //     System.err.println(standardEvaluation.evaluate(goldStandard, config));

                    SemevalWriter sw = new SemevalWriter(d.getId() + "-wng");
                    System.err.println("\n\tWriting results...");
                    sw.write(d, configs[numconfig].getAssignments());
                    System.err.println("done!");

                }
                numconfig++;
            }

            disambiguator.release();
            System.err.println("Desambiguation with WordNet glosses done");

        }




        if (backoff) {
            System.err.println("Backoff first sense");

            int numconfig = 0;
            for (Document d : dl) {

                if (toDisambiguate[numconfig]) {

                    System.err.println("Starting document " + d.getId());
                    System.err.println("\tLoading senses...");
                    if (!d.isAlreadyLoaded())
                        lrloader.loadSenses(d);

                    if (configs[numconfig] == null)
                        configs[numconfig] = firstSenseDisambiguator.disambiguate(d);
                    else
                        configs[numconfig] = firstSenseDisambiguator.disambiguate(d, configs[numconfig]);
                    //    System.err.println(standardEvaluation.evaluate(goldStandard, config));

                    SemevalWriter sw = new SemevalWriter(d.getId() + "-backoff");
                    System.err.println("\n\tWriting results...");
                    sw.write(d, configs[numconfig].getAssignments());
                    System.err.println("done!");
                    System.err.println("Backoff first sense done");
                }
                numconfig++;
            }
        }
    }

    public static void classicDisamb(String[] args, boolean toDisambiguate[], boolean useSemCor, boolean useDso, boolean useWNG, boolean useGMB, boolean backoff) throws IOException {

        CorpusLoader dl = new Semeval2007CorpusLoader(new FileInputStream("../data/senseval2007_task7/test/eng-coarse-all-words.xml"))
                .loadNonInstances(false);
        LRLoader lrloader = new WordnetLoader(new Dictionary(new URL("file", null ,"../data/wordnet/2.1/dict")));//.shuffle(true).extendedSignature(true);


        CorpusLoader dso = null;
        CorpusLoader semCor = null;
        CorpusLoader wng = null;
        CorpusLoader gmb = null;


        List<Text> taggedCorpora = new ArrayList<>();

        if (useGMB) {

            gmb = new GMBCorpusLoader("../data/GMB/gmb-2.2.0/", new Dictionary(new URL("../data/wordnet/2.1/dict")));
            gmb.load();
            for (Text t : gmb) {
                taggedCorpora.add(t);
            }
        }

        if (useSemCor) {
            semCor = new SemCorCorpusLoader("../data/semcor3.0/semcor_full.xml");
            //semCor = new SemCorCorpusLoader("../data/semcor3.0/semcor_big-sample.xml");
            //semCor = new SemCorCorpusLoader("../data/semcor3.0/semcor_full_sample.xml");
            semCor.load();
            for (Text t : semCor) {
                taggedCorpora.add(t);
            }
        }

        if (useDso) {
            dso = new DSOCorpusLoader("../data/dso", "../data/wordnet/2.1/dict", false);
            dso.load();
            for (Text t : dso) {
                taggedCorpora.add(t);
            }
        }

        if (useWNG) {
            wng = new WordnetGlossTagCorpusLoader("../data/glosstag");
            wng.load();
            for (Text t : wng) {
                taggedCorpora.add(t);
            }
        }

        WindowLoader wloader = new DocumentCollectionWindowLoader(taggedCorpora);
        wloader.load();

        List<ContextWindow> contextWindows = new ArrayList<>();
        contextWindows.add(new ContextWindowImpl(-1, -1));
        contextWindows.add(new ContextWindowImpl(1, 1));
        contextWindows.add(new ContextWindowImpl(-2, -2));
        contextWindows.add(new ContextWindowImpl(2, 2));
        contextWindows.add(new ContextWindowImpl(-2, -1));
        contextWindows.add(new ContextWindowImpl(-1, 1));
        contextWindows.add(new ContextWindowImpl(1, 2));
        contextWindows.add(new ContextWindowImpl(-3, -1));
        contextWindows.add(new ContextWindowImpl(-2, 1));
        contextWindows.add(new ContextWindowImpl(-1, 2));
        contextWindows.add(new ContextWindowImpl(1, 3));
        //contextWindows.add(new ContextWindowImpl(-4, -4));
        //contextWindows.add(new ContextWindowImpl(-5, -5));
        //contextWindows.add(new ContextWindowImpl(4, 4));
        //contextWindows.add(new ContextWindowImpl(5, 5));
        /*
        contextWindows.add(new ContextWindow(-5, 5));
        contextWindows.add(new ContextWindow(-4, 4));
        contextWindows.add(new ContextWindow(-3, 3));
        contextWindows.add(new ContextWindow(-2, 2));
        contextWindows.add(new ContextWindow(-1, 1));
        */

        System.err.println("Feature extraction");

        AggregateLocalTextFeatureExtractor altfe = new AggregateLocalTextFeatureExtractor();

        LocalCollocationFeatureExtractor lcfe = new LocalCollocationFeatureExtractor(contextWindows, false);
        altfe.addExtractor(lcfe);

        PosFeatureExtractor pfe = new PosFeatureExtractor(3, 3);
        altfe.addExtractor(pfe);

        LocalTextFeatureExtractor acfe = new LemmaFeatureExtractor(3, 3);
        altfe.addExtractor(acfe);

        //SingleWordSurroundingContextFeatureExtractor.buildIndex(taggedCorpora);
        //LocalTextFeatureExtractor acfe = new SingleWordSurroundingContextFeatureExtractor(3, 3);
        //altfe.addExtractor(acfe);

        TrainingDataExtractor trainingDataExtractor = new SemCorTrainingDataExtractor(altfe);
        trainingDataExtractor.extract(taggedCorpora);

        System.err.println("Feature extraction done");

        Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new SVMSetUp(), altfe, 32 , trainingDataExtractor);
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new BFTreeSetUp(), altfe, 16);
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new BayesianLogisticRegressionSetUp(), altfe, 16);
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new RBFNetworkSetUp(), altfe, 16);
       // Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new RandomForestSetUp(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])), altfe, Integer.parseInt(args[4]), trainingDataExtractor); //100 0 1 0 4
        //Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new NaiveBayesSetUp(Boolean.parseBoolean(args[0]), Boolean.parseBoolean(args[1])), altfe, Integer.parseInt(args[2]), trainingDataExtractor); //false false 4

        Disambiguator firstSenseDisambiguator = new FirstSenseDisambiguator();
        //GoldStandard goldStandard = new Semeval2007GoldStandard();
        //Evaluation standardEvaluation = new StandardEvaluation();

        System.err.println("Loading texts");
        dl.load();
        int i = 0;
        if (args.length == 1) {
            i = Integer.valueOf(args[0]) - 1;
        }
        int numconfig = 0;
        for (Document d : dl) {



            if (toDisambiguate[numconfig]) {
                System.err.println("Starting document " + d.getId());
                System.err.println("\tLoading senses...");
                lrloader.loadSenses(d);

                Configuration c = disambiguator.disambiguate(d);
                // System.err.println(standardEvaluation.evaluate(goldStandard,c));

                if (backoff)
                    c = firstSenseDisambiguator.disambiguate(d, c);
                // System.err.println(standardEvaluation.evaluate(goldStandard,c));

                SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
                System.err.println("\n\tWriting results...");
                sw.write(d, c.getAssignments());
                System.err.println("done!");
            }
            numconfig++;
        }
        disambiguator.release();
        //firstSenseDisambiguator.release();
        System.err.println("Disambiguation done");
    }
}
