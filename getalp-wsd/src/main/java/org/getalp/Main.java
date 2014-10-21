package org.getalp;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.loaders.document.Semeval2007DocumentLoader;
import org.getalp.disambiguation.loaders.resource.WordnetLoader;
import org.getalp.disambiguation.method.Disambiguator;
import org.getalp.disambiguation.method.legacy.LegacyWindowedLesk;
import org.getalp.disambiguation.result.SemevalWriter;
import org.getalp.similarity.local.SimilarityMeasure;
import org.getalp.similarity.local.string.TverskiIndex;

@SuppressWarnings("all")
public class Main {
    public static void main(String[] args) {
        Semeval2007DocumentLoader dl = new Semeval2007DocumentLoader("/Users/tchechem/wsgetalp/data/senseval2007_task7/test/eng-coarse-all-words.xml", false);
        WordnetLoader lrloader = new WordnetLoader("/Users/tchechem/wsgetalp/data/wordnet/2.1/dict", true, false);
        SimilarityMeasure sim;

        sim = new TverskiIndex(new ScaledLevenstein(), false, 1d, 0d, 0d, true, false, false, false);
        //sim = new SubmodularTverski(new ScaledLevenstein(),false,1,0d,0d,false,0.1);

        //Disambiguator sl = new LegacySimplifiedLesk(10,sim,false,false,false,false,false,false,true);
        Disambiguator sl = new LegacyWindowedLesk(6, sim, false, false);
        System.err.println("Loading texts");
        dl.load();
        for (Document d : dl.getDocuments()) {
            System.err.println("Starting document " + d.getId());
            System.err.println("\tLoading senses...");
            d.setSenses(lrloader.getAllSenses(d.getLexicalEntries()));
            //System.err.println("\tDisambiguating... ");
            Configuration c = sl.disambiguate(d);
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d, c);
            System.err.println("done!");
        }

    }
}
