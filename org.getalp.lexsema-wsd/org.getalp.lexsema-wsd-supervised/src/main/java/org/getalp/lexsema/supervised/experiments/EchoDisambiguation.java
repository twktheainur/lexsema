package org.getalp.lexsema.supervised.experiments;


import edu.mit.jwi.Dictionary;
import org.getalp.lexsema.io.annotresult.ConfigurationWriter;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.loader.SemCorCorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.EchoDisambiguator;
import org.getalp.lexsema.supervised.features.*;
import org.getalp.lexsema.supervised.features.extractors.AggregateLocalTextFeatureExtractor;
import org.getalp.lexsema.supervised.features.extractors.LemmaFeatureExtractor;
import org.getalp.lexsema.supervised.features.extractors.LocalCollocationFeatureExtractor;
import org.getalp.lexsema.supervised.features.extractors.PosFeatureExtractor;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EchoDisambiguation {
    private static Logger logger = LoggerFactory.getLogger(EchoDisambiguation.class);

    public static void main(String[] args) throws IOException {
        CorpusLoader dl = new Semeval2007CorpusLoader(new FileInputStream("../data/senseval2007_task7/test/eng-coarse-all-words.xml")).loadNonInstances(false);
        CorpusLoader semCor = new SemCorCorpusLoader("../data/semcor3.0/semcor_full.xml");
        LRLoader lrloader = new WordnetLoader(new Dictionary(new File("../data/wordnet/2.1/dict")))
                .extendedSignature(true).shuffle(false);

        semCor.load();
        WindowLoader wloader = new DocumentCollectionWindowLoader(semCor);
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

        LocalCollocationFeatureExtractor lcfe = new LocalCollocationFeatureExtractor(contextWindows);
        PosFeatureExtractor pfe = new PosFeatureExtractor(4, 4);
        LemmaFeatureExtractor acfe = new LemmaFeatureExtractor(4, 4);


        AggregateLocalTextFeatureExtractor altfe = new AggregateLocalTextFeatureExtractor();
        altfe.addExtractor(lcfe);
        altfe.addExtractor(pfe);
        altfe.addExtractor(acfe);

        TrainingDataExtractor trainingDataExtractor = new SemCorTrainingDataExtractor(altfe);
        trainingDataExtractor.extract(semCor);

        //Le dernier argument est la taille de la poole de threads
        // pour changer echo ou echo 2 changer dans EchoLexicalEntryDisambiguator
        Disambiguator disambiguator = new EchoDisambiguator("../data/supervised/", altfe, Integer.parseInt(args[0]), trainingDataExtractor);
        logger.info("Loading texts");
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
            ConfigurationWriter sw = new SemevalWriter(d.getId() + ".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d, c.getAssignments());
            System.err.println("done!");
        }
        disambiguator.release();
        disambiguator.release();
    }
}
