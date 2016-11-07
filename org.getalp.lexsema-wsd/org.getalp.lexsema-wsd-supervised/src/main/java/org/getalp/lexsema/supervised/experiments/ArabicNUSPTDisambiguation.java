package org.getalp.lexsema.supervised.experiments;


import edu.mit.jwi.Dictionary;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.SemCorCorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.ml.supervised.weka.NaiveBayesSetUp;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.WekaDisambiguator;
import org.getalp.lexsema.supervised.features.*;
import org.getalp.lexsema.supervised.features.extractors.*;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArabicNUSPTDisambiguation {
    private static Logger logger = LoggerFactory.getLogger(ArabicNUSPTDisambiguation.class);

    public static void main(String[] args) throws IOException {
        CorpusLoader dl = new Semeval2007CorpusLoader(new FileInputStream(args[0])).loadNonInstances(false);
        CorpusLoader semCor = new SemCorCorpusLoader(args[1]);
        //LRLoader lrloader = new DictionaryLRLoader(new FileInputStream(args[2]));
        LRLoader lrloader = new WordnetLoader(new Dictionary(new File("data/wordnet/2.1/dict/")));



//        LemmaFeatureExtractor lfe = new LemmaFeatureExtractor(3,1);
//        PosFeatureExtractor pfe = new PosFeatureExtractor(1, 2);
//        TargetPosLemmaFeatureExtractor tplfe = new TargetPosLemmaFeatureExtractor();
//
//        AggregateLocalTextFeatureExtractor altfe = new AggregateLocalTextFeatureExtractor();
//        altfe.addExtractor(lfe);
//        altfe.addExtractor(pfe);
//        altfe.addExtractor(tplfe);

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
        LocalCollocationFeatureExtractor lcfe = new LocalCollocationFeatureExtractor(contextWindows, false);
        PosFeatureExtractor pfe = new PosFeatureExtractor(3, 3);
        LocalTextFeatureExtractor acfe = new LemmaFeatureExtractor(3, 3, false);

        AggregateLocalTextFeatureExtractor altfe = new AggregateLocalTextFeatureExtractor();
        altfe.addExtractor(lcfe);
        altfe.addExtractor(pfe);
        altfe.addExtractor(acfe);

        TrainingDataExtractor trainingDataExtractor = new SemCorTrainingDataExtractor(altfe, true);
        trainingDataExtractor.extract(semCor);

        //Le dernier argument est la taille de la poole de threads
        WekaDisambiguator disambiguator = new WekaDisambiguator("../data/supervised", new NaiveBayesSetUp(true, true), altfe, 1, trainingDataExtractor);
        logger.info("Loading texts");
        dl.load();
        int i = 0;
        if (args.length == 1) {
            i = Integer.valueOf(args[0]) - 1;
        }
        for (Document d : dl) {

            System.err.println("Starting document " + d.getId());
            System.err.println("\tLoading senses... ");
            lrloader.loadSenses(d);

            Configuration c = disambiguator.disambiguate(d);
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d, c.getAssignments(), c.getIdAssignments());
            System.err.println("done!");
         //  System.exit(0);
        }
        disambiguator.release();
    }
}
