package org.getalp.lexsema.wsd.experiments;

import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.dictionary.DictionaryWriter;
import org.getalp.lexsema.io.dictionary.DocumentDictionaryWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureImpl;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.evaluation.*;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.FirstSenseDisambiguator;
import org.getalp.lexsema.wsd.method.sequencial.SimplifiedLesk;
import org.getalp.lexsema.wsd.method.sequencial.parameters.SimplifiedLeskParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SuppressWarnings("all")
public class IndexedSimplifiedLeskDisambiguation {

    private static Logger logger = LoggerFactory.getLogger(IndexedSimplifiedLeskDisambiguation.class);

    public IndexedSimplifiedLeskDisambiguation() {
    }

    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.currentTimeMillis();
        CorpusLoader dl = new Semeval2007CorpusLoader(new FileInputStream("../data/senseval2007_task7/test/eng-coarse-all-words-t1.xml")).loadNonInstances(true);
        LRLoader lrloader = new DictionaryLRLoader(new FileInputStream("../data/dictionnaires-lesk/dict-adapted-all-relations.xml"), true);
        SimilarityMeasure sim = new TverskiIndexSimilarityMeasureImpl();

        GoldStandard goldStandard = new Semeval2007GoldStandard();
        Evaluation evaluation = new StandardEvaluation();

        SimplifiedLeskParameters slp = new SimplifiedLeskParameters()
                .setAllowTies(true)
                .setIncludeTarget(false)
                .setOnlyUniqueWords(false)
                        //.setFallbackFS(true)
                .setMinimize(false);
        Disambiguator sl = new SimplifiedLesk(100, sim, slp, Runtime.getRuntime().availableProcessors());
        Disambiguator firstSenseDisambiguator = new FirstSenseDisambiguator();


        logger.info("Loading texts");
        dl.load();

        for (Document d : dl) {
            logger.info("Starting document " + d.getId());
            logger.info("\tLoading senses...");
            lrloader.loadSenses(d);
            logger.info("\tDisambiguating... ");
            Configuration c = sl.disambiguate(d);
            // c = firstSenseDisambiguator.disambiguate(d, c);

            WSDResult result = evaluation.evaluate(goldStandard, c);
            logger.info(result.toString());

            SemevalWriter sw = new SemevalWriter(d.getId() + ".ans");
            logger.info("\n\tWriting results...");
            sw.write(d, c.getAssignments());
            logger.info("done!");
        }
        DictionaryWriter writer = new DocumentDictionaryWriter(dl);
        writer.writeDictionary(new File("dictTest2.xml"));
        //sl.release();
        sl.release();
        long endTime = System.currentTimeMillis();
        System.out.println("Total elapsed time in execution of CombinedDisambiguation is : " + (endTime - startTime) + " ms.");

    }
}
