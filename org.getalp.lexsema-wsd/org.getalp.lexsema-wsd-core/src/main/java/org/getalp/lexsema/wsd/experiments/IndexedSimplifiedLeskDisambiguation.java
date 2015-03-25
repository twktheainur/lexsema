package org.getalp.lexsema.wsd.experiments;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.dictionary.DictionaryWriter;
import org.getalp.lexsema.io.dictionary.DocumentDictionaryWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.IndexedOverlapSimilarity;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.sequencial.WindowedLesk;
import org.getalp.lexsema.wsd.method.sequencial.parameters.SimplifiedLeskParameters;
import org.getalp.lexsema.wsd.method.sequencial.parameters.WindowedLeskParameters;

import java.io.File;

@SuppressWarnings("all")
public class IndexedSimplifiedLeskDisambiguation {
    public IndexedSimplifiedLeskDisambiguation() {
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words.xml").loadNonInstances(true);
        LRLoader lrloader = new DictionaryLRLoader(new File("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"));
        SimilarityMeasure sim = new IndexedOverlapSimilarity();

        SimplifiedLeskParameters slp = new SimplifiedLeskParameters()
                .setAddSenseSignatures(false)
                .setAllowTies(true)
                .setIncludeTarget(false)
                .setOnlyOverlapContexts(false)
                .setOnlyUniqueWords(false)
                        //.setFallbackFS(true)
                .setMinimize(false);
        Disambiguator sl = new WindowedLesk(2, sim, new WindowedLeskParameters(), 8);


        System.err.println("Loading texts");
        dl.load();

        for (Document d : dl) {
            System.err.println("Starting document " + d.getId());
            System.err.println("\tLoading senses...");
            lrloader.loadSenses(d);
            System.err.println("\tDisambiguating... ");
            Configuration c = sl.disambiguate(d);

            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            System.err.println("\n\tWriting results...");
            sw.write(d, c.getAssignments());
            System.err.println("done!");
        }
        DictionaryWriter writer = new DocumentDictionaryWriter(dl);
        writer.writeDictionary(new File("dictTest2.xml"));
        //sl.release();
        sl.release();
        long endTime = System.currentTimeMillis();
        System.out.println("Total elapsed time in execution of CombinedDisambiguation is : " + (endTime - startTime) + " ms.");

    }
}
