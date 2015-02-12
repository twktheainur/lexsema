package org.getalp.lexsema.supervised.experiments;


import org.getalp.lexsema.io.annotresult.ConfigurationWriter;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.SemCorTextLoader;
import org.getalp.lexsema.io.document.Semeval2013Task13TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.babelnet.BabelNetAPILoader;
import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.WekaDisambiguator;
import org.getalp.lexsema.supervised.features.*;
import org.getalp.lexsema.supervised.features.extractors.AggregateLocalTextFeatureExtractor;
import org.getalp.lexsema.supervised.features.extractors.LemmaFeatureExtractor;
import org.getalp.lexsema.supervised.features.extractors.LocalCollocationFeatureExtractor;
import org.getalp.lexsema.supervised.features.extractors.PosFeatureExtractor;
import org.getalp.lexsema.supervised.weka.NaiveBayesSetUp;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.FirstSenseDisambiguator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Semeval2013BabelBetWekaDisambiguationEnglish {
    private static Logger logger = LoggerFactory.getLogger(Semeval2013BabelBetWekaDisambiguationEnglish.class);

    public static void main(String[] args) throws IOException {
        TextLoader dl = new Semeval2013Task13TextLoader("../data/semeval-2013-task12-test-data/data/multilingual-all-words.en.xml")
                .loadNonInstances(false);
        TextLoader semCor = new SemCorTextLoader("../data/semcor3.0/semcor_full.xml");
        LRLoader lrloader = new BabelNetAPILoader(Language.ENGLISH).extendedSignature(false).shuffle(false).loadDefinitions(false).setLoadRelated(false);
        semCor.load();
        WindowLoader wloader = new DocumentCollectionWindowLoader(semCor);
        wloader.load();

        logger.info("Loading texts");
        dl.load();

        logger.info("Extracting features");
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
        LocalCollocationFeatureExtractor lcfe = new LocalCollocationFeatureExtractor(contextWindows);
        PosFeatureExtractor pfe = new PosFeatureExtractor(5, 5);
        LemmaFeatureExtractor acfe = new LemmaFeatureExtractor(5, 5);


        AggregateLocalTextFeatureExtractor altfe = new AggregateLocalTextFeatureExtractor();
        altfe.addExtractor(lcfe);
        altfe.addExtractor(pfe);
        altfe.addExtractor(acfe);

        TrainingDataExtractor trainingDataExtractor = new BabelNetSemCorTrainingDataExtractor(altfe, new File("../data/mapping/semcor_babelnet_mapping.en.csv"));
        //TrainingDataExtractor trainingDataExtractor = new SemCorTrainingDataExtractor(altfe);
        trainingDataExtractor.extract(semCor);

        //Le dernier argument est la taille de la poole de threads
        // pour changer echo ou echo 2 changer dans EchoLexicalEntryDisambiguator
        Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new NaiveBayesSetUp(true, true), altfe, 2, trainingDataExtractor);
        Disambiguator firstSense = new FirstSenseDisambiguator("../data/semeval-2013-task12-test-data/keys/baselines/babelnet/semcor.first-sense.en.key");
        int i = 0;
        if (args.length == 1) {
            i = Integer.valueOf(args[0]) - 1;
        }
        for (Document d : dl) {
            System.err.println("Starting document " + d.getId());
            System.err.println("\tLoading senses...");
            lrloader.loadSenses(d);

            Configuration c = disambiguator.disambiguate(d);
            //Configuration c = firstSense.disambiguate(d);
            c = firstSense.disambiguate(d, c);
            ConfigurationWriter sw = new SemevalWriter(d.getId() + ".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d, c.getAssignments());
            System.err.println("done!");
        }
        disambiguator.release();
        //disambiguator.release();
    }
}