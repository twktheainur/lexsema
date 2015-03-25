package org.getalp.lexsema.wsd.experiments;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.document.Semeval2007TextLoader;
import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.IndexedOverlapSimilarity;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.BatAlgorithm;
import org.getalp.lexsema.wsd.method.Disambiguator;

import java.io.File;

public class BatAlgorithmAntDictionaryDisambiguation {

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        TextLoader dl = new Semeval2007TextLoader("../data/senseval2007_task7/test/eng-coarse-all-words-t1.xml")
                .loadNonInstances(true);

        LRLoader lrloader = new DictionaryLRLoader(new File("/Users/tchechem/wsgetalp/data/dictionnaires-lesk/dict-adapted-all-relations.xml"));

        SimilarityMeasure sim = new IndexedOverlapSimilarity();

        Disambiguator batDisambiguator = new BatAlgorithm(sim);

        System.err.println("Loading texts");
        dl.load();

        for (Document d : dl) {

            System.err.println("Starting document " + d.getId());

            System.err.println("\tLoading senses...");
            lrloader.loadSenses(d);

            System.err.println("Disambiguating...");
            Configuration c = batDisambiguator.disambiguate(d);

            System.err.println("\n\tWriting results...");
            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            sw.write(d, c.getAssignments());

            System.err.println("done!");
        }

        batDisambiguator.release();

        long endTime = System.currentTimeMillis();
        System.out.println("Total time elapsed in execution of Bat Algorithm is : ");
        System.out.println((endTime - startTime) + " ms.");
    }
}
