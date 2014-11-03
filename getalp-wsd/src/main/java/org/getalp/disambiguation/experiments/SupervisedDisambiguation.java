package org.getalp.disambiguation.experiments;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.loaders.document.Semeval2007TextLoader;
import org.getalp.disambiguation.loaders.resource.WordnetLoader;
import org.getalp.disambiguation.method.SupervisedWeka;
import org.getalp.disambiguation.method.weka.SVMSetUp;
import org.getalp.disambiguation.result.SemevalWriter;

@SuppressWarnings("all")
public class SupervisedDisambiguation {
    public static void main(String[] args) {
        Semeval2007TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml", false);
        WordnetLoader lrloader = new WordnetLoader("../data/wordnet/2.1/dict", true, false);

        SupervisedWeka disambiguator = new SupervisedWeka(3, 1, 1, 2, "../data/supervised", new SVMSetUp());
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
    }
}
