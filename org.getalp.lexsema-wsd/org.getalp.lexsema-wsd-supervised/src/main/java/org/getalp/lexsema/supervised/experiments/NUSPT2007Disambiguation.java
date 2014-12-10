package org.getalp.lexsema.supervised.experiments;


import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.WordnetLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.WekaDisambiguator;
import org.getalp.lexsema.supervised.features.ContextWindow;
import org.getalp.lexsema.supervised.features.WindowLoader;
import org.getalp.lexsema.supervised.features.extractors.AggregateLocalTextFeatureExtractor;
import org.getalp.lexsema.supervised.features.extractors.AlignedContextFeatureExtractor;
import org.getalp.lexsema.supervised.features.extractors.LocalCollocationFeatureExtractor;
import org.getalp.lexsema.supervised.features.extractors.PosFeatureExtractor;
import org.getalp.lexsema.supervised.weka.NaiveBayesSetUp;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class NUSPT2007Disambiguation {
    public static void main(String[] args) throws IOException {
        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml")
                .loadNonInstances(false);
        WordnetLoader lrloader = new WordnetLoader("../data/wordnet/2.1/dict")
                .setShuffle(false).setHasExtendedSignature(true);

        WindowLoader wloader = new WindowLoader("../data/indexes/windows.csv");
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
        LocalCollocationFeatureExtractor lcfe = new LocalCollocationFeatureExtractor(contextWindows);
        PosFeatureExtractor pfe = new PosFeatureExtractor(3, 3);
        AlignedContextFeatureExtractor acfe = new AlignedContextFeatureExtractor(wloader);

        AggregateLocalTextFeatureExtractor altfe = new AggregateLocalTextFeatureExtractor();
        altfe.addExtractor(lcfe);
        altfe.addExtractor(pfe);
        altfe.addExtractor(acfe);

        Disambiguator disambiguator = new WekaDisambiguator("../data/supervised", new NaiveBayesSetUp(), altfe, 16);
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
