package org.getalp.lexsema.supervised.experiments;


import org.getalp.lexsema.io.annotresult.ConfigurationWriter;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.wordnet.WordnetLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.EchoDisambiguator;
import org.getalp.lexsema.supervised.features.extractors.*;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EchoDisambiguation {
    private static Logger logger = LoggerFactory.getLogger(EchoDisambiguation.class);

    public static void main(String[] args) throws IOException {
        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml").loadNonInstances(false);
        LRLoader lrloader = new WordnetLoader("../data/wordnet/2.1/dict").setHasExtendedSignature(true).setShuffle(false);
        LocalTextFeatureExtractor lfe = new LemmaFeatureExtractor(3, 1);
        LocalTextFeatureExtractor pfe = new PosFeatureExtractor(1, 2);
        LocalTextFeatureExtractor tplfe = new TargetPosLemmaFeatureExtractor();

        AggregateLocalTextFeatureExtractor altfe = new AggregateLocalTextFeatureExtractor();
        altfe.addExtractor(lfe);
        altfe.addExtractor(pfe);
        altfe.addExtractor(tplfe);

        //Le dernier argument est la taille de la poole de threads
        Disambiguator disambiguator = new EchoDisambiguator("../lexsema_pkg/data/semcor_features/", altfe, 1);
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
