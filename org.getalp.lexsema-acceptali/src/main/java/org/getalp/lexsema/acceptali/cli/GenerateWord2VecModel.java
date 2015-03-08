package org.getalp.lexsema.acceptali.cli;


import org.apache.commons.cli.*;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.EndingPreProcessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.SerializationUtils;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class GenerateWord2VecModel {

    private static final Logger logger = LoggerFactory.getLogger(GenerateWord2VecModel.class);
    private static final String OUTPUT_OPTION = "o";
    private static final String DEFAULT_OUTPUT = "word2vecModel";

    private static final Options options; // Command line op

    static {
        options = new Options();
        options.addOption("h", false, "Prints usage and exits. ");
        options.addOption(OUTPUT_OPTION, true, "The output file to which the model should be serialized");
    }

    private CommandLine cmd = null; // Command Line arguments

    private String location = DEFAULT_OUTPUT;
    private String targetDirectory = DEFAULT_OUTPUT;

    private SentenceIterator sentenceIterator;
    private TokenizerFactory tokenizer;
    private Word2Vec vec;


    private GenerateWord2VecModel() {
    }

    public static void main(String[] args) throws IOException, NoSuchVocableException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        @SuppressWarnings("LocalVariableOfConcreteClass") GenerateWord2VecModel generateWord2VecModel = new GenerateWord2VecModel();
        generateWord2VecModel.loadArgs(args);
        generateWord2VecModel.generateModel();
    }

    private static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        String help =
                String.format("Generates a translation closure and saves it to a directory that can be used as input for " +
                        "FileTranslationClosureGenerator%s", System.lineSeparator());
        formatter.printHelp("java -cp %spath%sto%slexsema-acceptali org.getalp.lexsema.acceptali.cli.GenerateWord2VecModel [OPTIONS] vocable source_language depth",
                "With OPTIONS in:", options,
                help, false);
    }


    private void loadArgs(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        CommandLineParser parser = new PosixParser();
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Error parsing arguments: " + e.getLocalizedMessage());
            printUsage();
            System.exit(1);
        }
        if (cmd.hasOption("h")) {
            printUsage();
            System.exit(0);
        }

        if (cmd.hasOption(OUTPUT_OPTION)) {
            targetDirectory = cmd.getOptionValue(OUTPUT_OPTION);
        }

        String[] remainingArgs = cmd.getArgs();

        if (remainingArgs.length < 1) {
            System.err.println("You must supply the input corpus");
            printUsage();
            System.exit(1);
        }

        location = remainingArgs[0];
        sentenceIterator = new LineSentenceIterator(new File(location));
        tokenizer = new DefaultTokenizerFactory();
    }


    private void generateModel() throws NoSuchVocableException, IOException {
        SentenceIterator iter = new LineSentenceIterator(new File(location));
        iter.setPreProcessor(new SentencePreProcessor() {
            @Override
            public String preProcess(String sentence) {
                return sentence.toLowerCase();
            }
        });

        //DocumentIterator sentenceIterator = new FileDocumentIterator(resource.getFile());
        TokenizerFactory t = new DefaultTokenizerFactory();
        final TokenPreProcess preProcessor = new EndingPreProcessor();
        t.setTokenPreProcessor(new TokenPreProcess() {
            @Override
            public String preProcess(String token) {
                String ltoken = token.toLowerCase();
                String base = preProcessor.preProcess(ltoken);
                base = base.replaceAll("\\d", "d");
                return base;
            }
        });

        logger.info("Training model...");
        VocabCache cache = new InMemoryLookupCache();

        int layerSize = 300;
        vec = new Word2Vec.Builder().sampling(1e-5)
                .minWordFrequency(5).batchSize(1000).useAdaGrad(false).layerSize(layerSize)
                .iterations(3).learningRate(0.025).minLearningRate(1e-2).negativeSample(10)
                .iterate(iter).tokenizerFactory(t).workers(Runtime.getRuntime().availableProcessors()).vocabCache(cache).build();
        vec.fit();


        logger.info("Testing model...");
        double sim = vec.similarity("people", "money");
        logger.info("Similarity between people and money " + sim);
        Collection<String> similar = vec.wordsNearest("day", 20);
        logger.info(similar.toString());

        File target = new File(targetDirectory);

        logger.info("Serializing model in " + target.getAbsolutePath());

        if (!target.exists()) {
            if (!target.mkdirs()) {
                logger.error(String.format("Cannot create: %s", target.getAbsolutePath()));
                System.exit(1);
            }
        }

        File cachePath = new File(targetDirectory, "cache.ser");
        File vecPath = new File(targetDirectory, "model.ser");

        SerializationUtils.saveObject(vec, vecPath);
        SerializationUtils.saveObject(cache, cachePath);

    }

}
