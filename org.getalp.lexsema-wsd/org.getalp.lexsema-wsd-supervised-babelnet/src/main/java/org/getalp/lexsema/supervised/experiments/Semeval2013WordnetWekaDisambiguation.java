package org.getalp.lexsema.supervised.experiments;


import edu.mit.jwi.Dictionary;
import org.getalp.lexsema.io.annotresult.ConfigurationWriter;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.SemCorCorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2013Task13CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Semeval2013WordnetWekaDisambiguation {
    private static Logger logger = LoggerFactory.getLogger(Semeval2013WordnetWekaDisambiguation.class);

    public static void main(String[] args) throws IOException {
        CorpusLoader dl = new Semeval2013Task13CorpusLoader("../data/semeval-2013-task12-test-data/data/multilingual-all-words.en.xml")
                .loadNonInstances(false);
        CorpusLoader semCor = new SemCorCorpusLoader("../data/semcor3.0/semcor_full.xml");
        //LRLoader lrloader = new BabelNetAPILoader(Language.ENGLISH).extendedSignature(false).shuffle(false).loadDefinitions(false).loadRelated(false);
        LRLoader lrloader = new WordnetLoader(new Dictionary(new File("../data/wordnet/3.0/dict")))
                .extendedSignature(true).shuffle(true);
        semCor.load();
        WindowLoader wloader = new DocumentCollectionWindowLoader(semCor);
        wloader.load();

        logger.info("Loading texts");
        dl.load();

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

        logger.info("Extracting features.");
        //TrainingDataExtractor trainingDataExtractor = new BabelLemmaTranslateSemCorTrainingDataExtractor(altfe,new File("../data/mapping/semcor_babelnet_mapping.en.csv"));TrainingDataExtractor trainingDataExtractor = new BabelLemmaTranslateSemCorTrainingDataExtractor(altfe,new File("../data/mapping/semcor_babelnet_mapping.en.csv"));
        TrainingDataExtractor trainingDataExtractor = new SemCorTrainingDataExtractor(altfe);
        trainingDataExtractor.extract(semCor);

        //Le dernier argument est la taille de la poole de threads
        // pour changer echo ou echo 2 changer dans EchoLexicalEntryDisambiguator
        Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new NaiveBayesSetUp(Boolean.parseBoolean(args[0]), Boolean.parseBoolean(args[1])), altfe, Integer.parseInt(args[2]), trainingDataExtractor);
        int i = 0;
        if (args.length == 1) {
            i = Integer.valueOf(args[0]) - 1;
        }
        for (Document d : dl) {
            System.err.println("Starting document " + d.getId());
            System.err.println("\tLoading senses...");
            lrloader.loadSenses(d);

            Configuration c = disambiguator.disambiguate(d);
            ConfigurationWriter sw = new SemevalWriter(d.getId() + ".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d, c.getAssignments());
            System.err.println("done!");
        }
        disambiguator.release();
        disambiguator.release();
    }
}
