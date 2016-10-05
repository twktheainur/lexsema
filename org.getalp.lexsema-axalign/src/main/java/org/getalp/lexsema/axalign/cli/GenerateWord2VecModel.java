package org.getalp.lexsema.axalign.cli;


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
import java.text.MessageFormat;
import java.util.Collection;
import java.util.regex.Pattern;

@SuppressWarnings("ClassWithTooManyFields")
public final class GenerateWord2VecModel {

    private static final double DEFAULT_SAMPLING = 1e-5;
    private static final int DEFAULT_MIN_WORD_FREQUENCY = 5;
    private static final int DEFAULT_LAYER_SIZE = 300;
    private static final int DEFAULT_ITERATIONS = 3;
    private static final double DEFAULT_LEARNING_RATE = 0.025;
    private static final double DEFAULT_MIN_LEARNING_RATE = 1e-2;
    private static final int DEFAULT_NEGATIVE_SAMPLE = 10;
    private static final int DEFAULT_BATCH_SIZE = 1000;
    private static final boolean DEFAULT_USE_ADA_GRAD = false;

    private static final String DEFAULT_OUTPUT = "word2vecModel";
    private static final int DEFAULT_WORKERS = Runtime.getRuntime().availableProcessors();


    // .minWordFrequency(5).batchSize(1000).useAdaGrad(false).layerSize(layerSize)
    //.iterations(3).learningRate(0.025).minLearningRate(1e-2).negativeSample(10)
    //.iterate(iter).tokenizerFactory(t).workers(Runtime.getRuntime().availableProcessors()).vocabCache(cache).build();
    private static final int TEST_NEAREST_WORD_VALUE = 20;
    private static final Logger logger = LoggerFactory.getLogger(GenerateWord2VecModel.class);
    private static final String OUTPUT_OPTION = "o";
    private static final String SAMPLING_OPTION = "sp";
    private static final String MIN_WORD_FREQUENCY_OPTION = "mf";
    private static final String LAYER_SIZE_OPTION = "ls";
    private static final String ITERATIONS_OPTION = "i";
    private static final String LEARNING_RATE_OPTION = "lr";
    private static final String MIN_LEARNING_RATE_OPTION = "mf";
    private static final String NEGATIVE_SAMPLE_OPTION = "ns";
    private static final String USE_ADA_GRAD_OPTION = "mf";
    private static final String WORKERS_OPTION = "nt";
    private static final String BATCH_SIZE_OPTION = "bs";
    private static final Options options; // Command line op
    private static final Pattern DIGITS = Pattern.compile("\\d");

    static {
        options = new Options();
        options.addOption("h", false, "Prints usage and exits. ");
        options.addOption(OUTPUT_OPTION, true, "The output file to which the model should be serialized");
        options.addOption(SAMPLING_OPTION, true, "Set the sampling delay (default 10^-5");
        options.addOption(MIN_WORD_FREQUENCY_OPTION, true, "Set the minimum word frequency for skip grams to be considered (default 5)");
        options.addOption(LAYER_SIZE_OPTION, true, "Set the hidden layer size (default 300)");
        options.addOption(ITERATIONS_OPTION, true, "Set the number of iterations (default 3)");
        options.addOption(LEARNING_RATE_OPTION, true, "Set the learning rate (default 0.025)");
        options.addOption(MIN_LEARNING_RATE_OPTION, true, "Set the minimum learning rate (default 10^-2)");
        options.addOption(NEGATIVE_SAMPLE_OPTION, true, "Set the number of negative samples (default 10)");
        options.addOption(BATCH_SIZE_OPTION, true, "Set the batch size (default 1000)");
        options.addOption(USE_ADA_GRAD_OPTION, false, "Activate ADA GRAD (default off)");
        options.addOption(WORKERS_OPTION, true, "Set the number of worker threads for training (default all available processors cores)");

    }

    private double sampling = DEFAULT_SAMPLING;
    private int minWordFrequency = DEFAULT_MIN_WORD_FREQUENCY;
    private int layerSize = DEFAULT_LAYER_SIZE;
    private int iterations = DEFAULT_ITERATIONS;
    private double learningRate = DEFAULT_LEARNING_RATE;
    private double minLearningRate = DEFAULT_MIN_LEARNING_RATE;
    private int negativeSample = DEFAULT_NEGATIVE_SAMPLE;
    private int batchSize = DEFAULT_BATCH_SIZE;
    private boolean useAdaGrad = DEFAULT_USE_ADA_GRAD;
    private int workers = DEFAULT_WORKERS;
    private CommandLine cmd; // Command Line arguments

    private String targetDirectory = DEFAULT_OUTPUT;

    private SentenceIterator sentenceIterator;
    private TokenizerFactory tokenizer;


    private GenerateWord2VecModel() {
    }

    public static void main(String... args) throws IOException, NoSuchVocableException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        @SuppressWarnings("LocalVariableOfConcreteClass") GenerateWord2VecModel generateWord2VecModel = new GenerateWord2VecModel();
        generateWord2VecModel.loadArgs(args);
        generateWord2VecModel.generateModel();
    }

    private static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        String help =
                String.format("Generates a translation closure and saves it to a directory that can be used as input for FileTranslationClosureGenerator%s", System.lineSeparator());
        formatter.printHelp("java -cp %spath%sto%slexsema-acceptali org.getalp.lexsema.acceptali.cli.GenerateWord2VecModel [OPTIONS] vocable source_language depth",
                "With OPTIONS in:", options,
                help, false);
    }


    private void loadArgs(String... args) throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        CommandLineParser parser = new PosixParser();
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                printUsage();
                System.exit(0);
            }

            if (cmd.hasOption(OUTPUT_OPTION)) {
                targetDirectory = cmd.getOptionValue(OUTPUT_OPTION);
            }
            loadWord2VecOptions();
            if (cmd.hasOption(WORKERS_OPTION)) {
                workers = Integer.valueOf(cmd.getOptionValue(WORKERS_OPTION));
            }

            String[] remainingArgs = cmd.getArgs();
            if (remainingArgs.length < 1) {
                logger.error("You must supply the input corpus");
                printUsage();
                System.exit(1);
            }

            sentenceIterator = new LineSentenceIterator(new File(remainingArgs[0]));
            sentenceIterator.setPreProcessor((SentencePreProcessor) String::toLowerCase);

            tokenizer = new DefaultTokenizerFactory();
            final TokenPreProcess preProcessor = new EndingPreProcessor();
            tokenizer.setTokenPreProcessor(token -> {
                String lToken = token.toLowerCase();
                String base = preProcessor.preProcess(lToken);
                base = DIGITS.matcher(base).replaceAll("d");
                return base;
            });
        } catch (ParseException e) {
            logger.error(MessageFormat.format("Error parsing arguments: {0}", e.getLocalizedMessage()));
            printUsage();
            System.exit(1);
        }
    }

    private void loadWord2VecOptions() {
        if (cmd.hasOption(SAMPLING_OPTION)) {
            sampling = Double.valueOf(cmd.getOptionValue(SAMPLING_OPTION));
        }
        if (cmd.hasOption(MIN_WORD_FREQUENCY_OPTION)) {
            minWordFrequency = Integer.valueOf(cmd.getOptionValue(MIN_WORD_FREQUENCY_OPTION));
        }
        if (cmd.hasOption(LEARNING_RATE_OPTION)) {
            learningRate = Double.valueOf(cmd.getOptionValue(LEARNING_RATE_OPTION));
        }
        if (cmd.hasOption(MIN_LEARNING_RATE_OPTION)) {
            minLearningRate = Double.valueOf(cmd.getOptionValue(MIN_LEARNING_RATE_OPTION));
        }
        if (cmd.hasOption(LAYER_SIZE_OPTION)) {
            layerSize = Integer.valueOf(cmd.getOptionValue(LAYER_SIZE_OPTION));
        }
        if (cmd.hasOption(ITERATIONS_OPTION)) {
            iterations = Integer.valueOf(cmd.getOptionValue(ITERATIONS_OPTION));
        }
        if (cmd.hasOption(NEGATIVE_SAMPLE_OPTION)) {
            negativeSample = Integer.valueOf(cmd.getOptionValue(NEGATIVE_SAMPLE_OPTION));
        }
        if (cmd.hasOption(BATCH_SIZE_OPTION)) {
            batchSize = Integer.valueOf(cmd.getOptionValue(BATCH_SIZE_OPTION));
        }
        if (cmd.hasOption(USE_ADA_GRAD_OPTION)) {
            useAdaGrad = true;
        }
    }


    private void generateModel() throws NoSuchVocableException, IOException {


        logger.info("Training model...");
        VocabCache cache = new InMemoryLookupCache();


        Word2Vec vec = new Word2Vec.Builder().sampling(sampling)
                .minWordFrequency(minWordFrequency).batchSize(batchSize).useAdaGrad(useAdaGrad).layerSize(layerSize)
                .iterations(iterations).learningRate(learningRate).minLearningRate(minLearningRate).negativeSample(negativeSample)
                .iterate(sentenceIterator).tokenizerFactory(tokenizer).workers(workers).vocabCache(cache).build();
        vec.fit();


        logger.info("Testing model...");
        double sim = vec.similarity("people", "money");
        logger.info("Similarity between people and money {}", sim);
        Collection<String> similar = vec.wordsNearest("day", TEST_NEAREST_WORD_VALUE);
        logger.info(similar.toString());

        File target = new File(targetDirectory);

        logger.info("Serializing model in {}", target.getAbsolutePath());

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
