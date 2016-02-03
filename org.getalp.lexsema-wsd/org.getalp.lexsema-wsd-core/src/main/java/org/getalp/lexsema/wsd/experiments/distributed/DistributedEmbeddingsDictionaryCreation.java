package org.getalp.lexsema.wsd.experiments.distributed;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.getalp.lexsema.io.annotresult.ConfigurationWriter;
import org.getalp.lexsema.io.annotresult.SemevalWriter;
import org.getalp.lexsema.io.dictionary.DictionaryWriter;
import org.getalp.lexsema.io.dictionary.DocumentDictionaryWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.io.word2vec.SerializedModelWord2VecLoader;
import org.getalp.lexsema.io.word2vec.Word2VecLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.measures.lesk.AnotherLeskSimilarity;
import org.getalp.lexsema.similarity.signatures.enrichment.Word2VecLocalSignatureEnrichment;
import org.getalp.lexsema.util.distribution.SparkSingleton;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.evaluation.Evaluation;
import org.getalp.lexsema.wsd.evaluation.GoldStandard;
import org.getalp.lexsema.wsd.evaluation.Semeval2007GoldStandard;
import org.getalp.lexsema.wsd.evaluation.StandardEvaluation;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.MultiThreadCuckooSearch;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.DistributedConfigurationScorerWithCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public final class DistributedEmbeddingsDictionaryCreation
{
    private static final Logger logger = LoggerFactory.getLogger(DistributedEmbeddingsDictionaryCreation.class);
    private static final long MS_IN_S = 1000L;
    private static final long MS_IN_M = 60000L;

    private DistributedEmbeddingsDictionaryCreation() {
    }

    private static CorpusLoader loadCorpus(String resourceURI){

        CorpusLoader corpusLoader = new Semeval2007CorpusLoader(DistributedEmbeddingsDictionaryCreation.class.getResourceAsStream(resourceURI));
        corpusLoader.load();
        return corpusLoader;
    }

    private static LRLoader loadLexicalResource(String resourceURI, String modelURI) throws IOException {

        URL model = DistributedEmbeddingsDictionaryCreation.class.getResource(modelURI);

        Word2VecLoader word2VecLoader = new SerializedModelWord2VecLoader();
        word2VecLoader.loadGoogle(new File(model.getPath()),true, true);
        WordVectors vectors = word2VecLoader.getWordVectors();

        return new DictionaryLRLoader(DistributedEmbeddingsDictionaryCreation.class.getResourceAsStream(resourceURI), false,
                new Word2VecLocalSignatureEnrichment(vectors,10)).index(true).distributed(true);
    }

    private static void loadSensesForDocument(Iterable<Text> corpusLoader, LRLoader lrLoader){

        for(Text document: corpusLoader) {
            logger.info("\tLoading senses for {}...",document.getId());
            lrLoader.loadSenses(document);
        }
    }

    private static void writeDictionary(CorpusLoader corpusLoader, File path){
        DictionaryWriter writer = new DocumentDictionaryWriter(corpusLoader);
        writer.writeDictionary(path);
    }

    public static void main(String... args) throws java.io.IOException {

        SparkSingleton.initialize("spark://localhost:12345", "DistributedDictionaryCreation");

        long startTime = System.currentTimeMillis();

        logger.info("Loading corpus...");
        CorpusLoader corpusLoader = loadCorpus("/semeval2007/eng-coarse-all-words.xml");

        logger.info("Loading lexical resource...");
        LRLoader lrLoader = loadLexicalResource("/wordnet_full_dict.xml", "/word2vec/eng");

        loadSensesForDocument(corpusLoader, lrLoader);

        writeDictionary(corpusLoader,new File("embeddings_dict_distr.xml"));

        long endTime = System.currentTimeMillis();
        logger.info("Total time elapsed in execution of Cuckoo Search Algorithm is : ");
        logger.info("{} ms.", endTime - startTime);
        logger.info("{} s.", (endTime - startTime) / MS_IN_S);
        logger.info("{} m.", (endTime - startTime) / MS_IN_M);
    }
}
