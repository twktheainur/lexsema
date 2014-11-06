package org.getalp.disambiguation.experiments;

import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.method.features.ContextWindow;
import org.getalp.disambiguation.method.features.WindowLoader;
import org.getalp.disambiguation.method.features.extractors.AggregateLocalTextFeatureExtractor;
import org.getalp.disambiguation.method.features.extractors.AlignedContextFeatureExtractor;
import org.getalp.disambiguation.method.features.extractors.LocalCollocationFeatureExtractor;
import org.getalp.disambiguation.method.features.extractors.PosFeatureExtractor;
import org.getalp.disambiguation.method.sequencial.WekaDisambuguator;
import org.getalp.disambiguation.method.weka.SVMSetUp;
import org.getalp.disambiguation.result.SemevalWriter;
import org.getalp.io.Document;
import org.getalp.io.document.Semeval2007TextLoader;
import org.getalp.io.resource.WordnetLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class NUSPT2007Disambiguation {
    public static void main(String[] args) throws IOException {
        Semeval2007TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml", false);
        WordnetLoader lrloader = new WordnetLoader("../data/wordnet/2.1/dict", true, false);

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

        WekaDisambuguator disambiguator = new WekaDisambuguator("../data/supervised", new SVMSetUp(), altfe, 16);
        System.err.println("Loading texts");
        dl.load();
        int i = 0;
        if (args.length == 1) {
            i = Integer.valueOf(args[0]) - 1;
        }
        for (; i < dl.getTexts().size(); i++) {
            Document d = dl.getTexts().get(i);
            System.err.println("Starting document " + d.getId());
            System.err.println("\tLoading senses...");
            d.setSenses(lrloader.getAllSenses(d.getLexicalEntries()));
            //System.err.println("\tDisambiguating... ");
            Configuration c = disambiguator.disambiguate(d);
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d, c);
            System.err.println("done!");
        }
        disambiguator.release();
    }
}
