package org.getalp;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.loaders.document.Semeval2007DocumentLoader;
import org.getalp.disambiguation.loaders.resource.WordnetLoader;
import org.getalp.disambiguation.method.Disambiguator;
import org.getalp.disambiguation.method.sequencial.SimplifiedLesk;
import org.getalp.disambiguation.method.sequencial.WindowedLesk;
import org.getalp.disambiguation.method.sequencial.parameters.SimplifiedLeskParameters;
import org.getalp.disambiguation.method.sequencial.parameters.WindowedLeskParameters;
import org.getalp.disambiguation.result.SemevalWriter;
import org.getalp.similarity.local.SimilarityMeasure;
import org.getalp.similarity.local.string.SubmodularTverski;

@SuppressWarnings("all")
public class ParallelMain {
    public ParallelMain() {
    }

    public static void main(String[] args) {
        Semeval2007DocumentLoader dl = new Semeval2007DocumentLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml", false);
        WordnetLoader lrloader = new WordnetLoader("../data/wordnet/2.1/dict", true, false);
        SimilarityMeasure sim_lr_hp;
        SimilarityMeasure sim_full;

        //sim_lr_hp = new TverskiIndex(new ScaledLevenstein(),false, 1d, 0d, 0d, true, true,false ,true);
        sim_lr_hp = new SubmodularTverski(new ScaledLevenstein(), true, 0.1d, 0.5d, 0.5d, true, false, false, false, 0.1, false, false, false, false);

        SimplifiedLeskParameters slp = new SimplifiedLeskParameters(false, false, false, false, false, false, false);
        Disambiguator sl = new SimplifiedLesk(10, sim_lr_hp, slp, 4);


        WindowedLeskParameters wlp = new WindowedLeskParameters(false, false);
        sim_full = new SubmodularTverski(new ScaledLevenstein(), true, 1d, 0.5d, 0.5d, true, false, false, false, 0.1, false, false, false, false);
        Disambiguator sl_full = new WindowedLesk(2, sim_full, wlp, 4);


        //Disambiguator sl = new LegacySimplifiedLesk(10,sim_lr_hp,);
        //WindowedLeskParameters wlp = new WindowedLeskParameters(false,false);
        //Disambiguator sl = new WindowedLesk(6, sim_lr_hp, wlp, 4);
        System.err.println("Loading texts");
        dl.load();


        for (Document d : dl.getDocuments()) {
            System.err.println("Starting document " + d.getId());
            System.err.println("\tLoading senses...");
            d.setSenses(lrloader.getAllSenses(d.getLexicalEntries()));
            //System.err.println("\tDisambiguating... ");
            System.err.println("Applying low recall high precision simplified lesk...");
            Configuration c = sl.disambiguate(d);

            System.err.println("Completing with average precision/recall simplified lesk...");
            c = sl_full.disambiguate(d, c);

            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d, c);
            System.err.println("done!");
        }
        sl.release();
        sl_full.release();
    }
}
