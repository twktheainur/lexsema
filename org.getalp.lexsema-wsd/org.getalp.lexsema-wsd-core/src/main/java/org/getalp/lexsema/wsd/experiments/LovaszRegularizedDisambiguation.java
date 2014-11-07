package org.getalp.lexsema.wsd.experiments;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.io.Document;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.resource.WordnetLoader;
import org.getalp.lexsema.similarity.SimilarityMeasure;
import org.getalp.lexsema.similarity.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.util.VisualVMTools;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.sequencial.SimplifiedLesk;
import org.getalp.lexsema.wsd.method.sequencial.parameters.SimplifiedLeskParameters;
import org.getalp.lexsema.wsd.result.SemevalWriter;

@SuppressWarnings("all")
public class LovaszRegularizedDisambiguation {
    public LovaszRegularizedDisambiguation() {
    }

    public static void main(String[] args) {

        VisualVMTools.delayUntilReturn();

        Semeval2007TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml", false);
        WordnetLoader lrloader = new WordnetLoader("../data/wordnet/2.1/dict", true, true);
        SimilarityMeasure similarityMeasure;

        similarityMeasure = new TverskiIndexSimilarityMeasureBuilder()
                .distance(new ScaledLevenstein())
                .computeRatio(false)
                .alpha(1d)
                .beta(0.0d)
                .gamma(0.0d)
                .fuzzyMatching(true)
                .quadraticWeighting(false)
                .extendedLesk(false)
                .randomInit(false)
                .regularizeOverlapInput(false)
                .optimizeOverlapInput(false)
                .regularizeRelations(false)
                .optimizeRelations(false)
                .isDistance(true)
                .build();

        /*WindowedLeskParameters algorithmParameters = new WindowedLeskParameters()
                .setFallbackFS(false)
                .setMinimize(true);*/
        //Disambiguator sl = new WindowedLesk(2, similarityMeasure, algorithmParameters, 1);

        SimplifiedLeskParameters algorithmParameters = new SimplifiedLeskParameters()
                .setMinimize(true);

        Disambiguator disambiguator = new SimplifiedLesk(1000, similarityMeasure, algorithmParameters, 1);


        //Disambiguator sl = new LegacySimplifiedLesk(10,sim_lr_hp,);
        //WindowedLeskParameters wlp = new WindowedLeskParameters(false,false);
        //Disambiguator sl = new WindowedLesk(6, sim_lr_hp, wlp, 4);
        System.err.println("Loading texts");
        dl.load();


        for (Document d : dl.getTexts()) {
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
