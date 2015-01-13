package org.getalp.lexsema.wsd.experiments;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2013Task13TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dbnary.DBNaryLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.util.VisualVMTools;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.sequencial.SimplifiedLesk;
import org.getalp.lexsema.wsd.method.sequencial.parameters.SimplifiedLeskParameters;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("all")
public class DbnaryDisambiguationSimplifiedLesk {

    public static final String DB_PATH = ".." + File.separatorChar + "data" + File.separatorChar + "dbnary" + File.separatorChar + "dbnarutdb_fr_en";
    public static final String ONTOLOGY_PROPERTIES = "data" + File.separatorChar + "ontology.properties";

    public DbnaryDisambiguationSimplifiedLesk() {
    }

    public static void main(String[] args) throws NoSuchMethodException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        VisualVMTools.delayUntilReturn();

        TextLoader dl = new Semeval2013Task13TextLoader("../data/semeval-2013-task12-test-data/data/multilingual-all-words.en.xml")
                .loadNonInstances(false);
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


        for (Document d : dl) {
            LRLoader lrloader = new DBNaryLoader(DB_PATH, ONTOLOGY_PROPERTIES, d.getLanguage()).setShuffle(true);
            System.err.println("Starting document " + d.getId());
            System.err.println("\tLoading senses...");
            lrloader.loadSenses(d);
            //System.err.println("\tDisambiguating... ");
            Configuration c = disambiguator.disambiguate(d);
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d, c.getAssignments());
            System.err.println("done!");
        }
        disambiguator.release();
    }
}
