package org.getalp.lexsema.wsd.experiments.distributed;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.getalp.lexsema.io.dictionary.DictionaryWriter;
import org.getalp.lexsema.io.dictionary.DocumentDictionaryWriter;
import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.io.document.loader.Semeval2007CorpusLoader;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dictionary.DictionaryLRLoader;
import org.getalp.lexsema.io.word2vec.SerializedModelWord2VecLoader;
import org.getalp.lexsema.io.word2vec.Word2VecLoader;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.signatures.enrichment.Word2VecLocalSignatureEnrichment;
import org.getalp.lexsema.util.distribution.SparkSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileSystem;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class DistributedEmbeddingsDictionaryCreation {
    private static final Logger logger = LoggerFactory.getLogger(DistributedEmbeddingsDictionaryCreation.class);
    private static final long MS_IN_S = 1000L;
    private static final long MS_IN_M = 60000L;
    private static final int TOP_N = 30;

    private DistributedEmbeddingsDictionaryCreation() {
    }

    private static CorpusLoader loadCorpus(String resourceURI) {

        CorpusLoader corpusLoader = new Semeval2007CorpusLoader(DistributedEmbeddingsDictionaryCreation.class.getResourceAsStream(resourceURI));
        corpusLoader.load();
        return corpusLoader;
    }

    @SuppressWarnings("resource")
    private static File materializeModel(String resourceURI) throws IOException {

        String mURI = DistributedEmbeddingsDictionaryCreation.class.getResource(String.format("%s/model.bin", resourceURI)).toString();

        Path targetModelDir;
        final Map<String, String> env = new HashMap<>();
        String[] array;
        FileSystem resourceFileSystem;
        if(mURI.contains("!")){
            array = mURI.split("!");
            resourceFileSystem = FileSystems.newFileSystem(URI.create(array[0]), env);
        } else {
            array = mURI.split(":");
            resourceFileSystem = FileSystems.getDefault();
        }
            final Path path = resourceFileSystem.getPath(array[1]);
            try (InputStream inputStream = Files.newInputStream(path)) {
                targetModelDir = Files.createTempDirectory("materializedResource");
                Path targetModel = targetModelDir.resolve("model.bin");
                Files.copy(inputStream,targetModel);
            }
        return targetModelDir.toFile();
    }

    @SuppressWarnings("LawOfDemeter")
    private static LRLoader loadLexicalResource(String resourceURI, String modelURI) throws IOException {

        File modelDir = materializeModel(modelURI);
        Word2VecLoader word2VecLoader = new SerializedModelWord2VecLoader();
        word2VecLoader.loadGoogle(modelDir, true, true);
        WordVectors vectors = word2VecLoader.getWordVectors();

        return new DictionaryLRLoader(DistributedEmbeddingsDictionaryCreation.class.getResourceAsStream(resourceURI), false,
                new Word2VecLocalSignatureEnrichment(vectors, TOP_N)).index(true).distributed(true);
    }

    private static void loadSensesForDocument(Iterable<Text> corpusLoader, LRLoader lrLoader) {

        for (Text document : corpusLoader) {
            logger.info("\tLoading senses for {}...", document.getId());
            lrLoader.loadSenses(document);
        }
    }

    private static void writeDictionary(CorpusLoader corpusLoader, File path) {
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

        writeDictionary(corpusLoader, new File("/home/tchechem/embeddings_dict_distr.xml"));

        long endTime = System.currentTimeMillis();
        logger.info("Total time elapsed in execution of Cuckoo Search Algorithm is : ");
        logger.info("{} ms.", endTime - startTime);
        logger.info("{} s.", (endTime - startTime) / MS_IN_S);
        logger.info("{} m.", (endTime - startTime) / MS_IN_M);
    }
}
