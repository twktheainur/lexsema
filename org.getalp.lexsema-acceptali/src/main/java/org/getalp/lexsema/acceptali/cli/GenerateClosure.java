package org.getalp.lexsema.acceptali.cli;


import org.apache.commons.cli.*;
import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureGenerator;
import org.getalp.lexsema.acceptali.closure.generator.TranslationClosureGeneratorFactory;
import org.getalp.lexsema.acceptali.closure.writer.FileTranslationClosureWriter;
import org.getalp.lexsema.acceptali.closure.writer.TranslationClosureWriter;
import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.*;
import org.getalp.lexsema.ontolex.graph.store.Store;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class GenerateClosure {

    private static final String ONTOLOGY_PROPERTIES = "data" + File.separatorChar + "ontology.properties";
    private static final String LANGUAGE_OPTION = "l";
    private static final String STORAGE_TYPE_OPTION = "st";
    private static final String STORAGE_LOCATION_OPTION = "sl";
    private static final String OUTPUT_OPTION = "o";
    private static final String DEFAULT_LANGUAGES = "fr,en,de,it";
    private static final String DEFAULT_OUTPUT = "closureOutput";
    private static final String DEFAULT_STORAGE_TYPE = "tdb";

    private static final Options options; // Command line op

    static {
        options = new Options();
        options.addOption("h", false, "Prints usage and exits. ");
        options.addOption(LANGUAGE_OPTION, true,
                String.format("Languages (fr, en, de, pt, ru, bg, tr, es, it, fi, el, pl, jp). %s by default.", DEFAULT_LANGUAGES));
        options.addOption(STORAGE_TYPE_OPTION, true, "Type of the triple store (tdb, virtuoso, file, remote [default=tdb]");
        options.addOption(STORAGE_LOCATION_OPTION, true, "The location of the store (file, url, jdbc string)");
        options.addOption(OUTPUT_OPTION, true, "The output directory to which to write the closure");
    }

    private CommandLine cmd = null; // Command Line arguments

    private DBNary dbNary;

    private String location = DEFAULT_OUTPUT;
    private Store vts;
    private String targetDirectory = DEFAULT_OUTPUT;

    private String vocable;
    private int depth;
    private String sourceLanguage;


    private GenerateClosure() {
    }

    public static void main(String[] args) throws IOException, NoSuchVocableException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        @SuppressWarnings("LocalVariableOfConcreteClass") GenerateClosure generateClosure = new GenerateClosure();
        generateClosure.loadArgs(args);
        generateClosure.writeClosure(generateClosure.generateClosure());
    }

    private static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        String help =
                String.format("Generates a translation closure and saves it to a directory that can be used as input for " +
                        "FileTranslationClosureGenerator%s", System.lineSeparator());
        formatter.printHelp("java -cp %spath%sto%slexsema-acceptali org.getalp.lexsema.acceptali.cli.GenerateClosure [OPTIONS] vocable source_language depth",
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

        processStorageOptions();

        String cmdLanguages = DEFAULT_LANGUAGES;

        String[] languageCodeList = cmdLanguages.split(",");
        Language[] languages = new Language[languageCodeList.length];
        for (int i = 0; i < languageCodeList.length; i++) {
            languages[i] = Language.fromCode(languageCodeList[i]);
        }

        if (cmd.hasOption(OUTPUT_OPTION)) {
            targetDirectory = cmd.getOptionValue(OUTPUT_OPTION);
        }

        String[] remainingArgs = cmd.getArgs();

        if (remainingArgs.length < 3) {
            System.err.println("You must supply the vocable, source_language and the depth");
            printUsage();
            System.exit(1);
        }

        vocable = remainingArgs[0];

        sourceLanguage = remainingArgs[1];

        depth = Integer.valueOf(remainingArgs[2]);

        OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        dbNary = (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, languages);
    }

    private void processStorageOptions() throws IOException {
        if (!cmd.hasOption(STORAGE_LOCATION_OPTION)) {
            System.err.println("You must supply a storage location (-sl option)");
            printUsage();
            System.exit(1);
        } else {
            location = cmd.getOptionValue(STORAGE_LOCATION_OPTION);
        }

        String storageType = DEFAULT_STORAGE_TYPE;

        if (cmd.hasOption(STORAGE_TYPE_OPTION)) {
            storageType = cmd.getOptionValue(STORAGE_TYPE_OPTION);
        }
        //tdb, virtuoso, file, remote
        switch (storageType) {
            case "tdb":
                vts = new JenaTDBStore(location);
                break;
            case "virtuoso":
                String user = location.split("@")[0].split(":")[0];
                String password = location.split("@")[0].split(":")[1];
                String uri = location.split("@")[1];
                vts = new VirtuosoStore(uri, user, password);
                break;
            case "file":
                vts = new JenaMemoryStore(location);
                break;
            case "remote":
                vts = new JenaRemoteSPARQLStore(location);
                break;
        }
        StoreHandler.registerStoreInstance(vts);
    }

    private LexicalResourceTranslationClosure<LexicalSense> generateClosure() throws NoSuchVocableException {
        Vocable v = dbNary.getVocable(vocable, Language.fromCode(sourceLanguage));
        List<LexicalEntry> ventries = dbNary.getLexicalEntries(v);
        if (!ventries.isEmpty()) {
            TranslationClosureGenerator gtc = TranslationClosureGeneratorFactory.createCompositeGenerator(dbNary, ventries.get(0));
            return generateEntryClosure(gtc);
        }
        return null;
    }

    private LexicalResourceTranslationClosure<LexicalSense> generateEntryClosure(TranslationClosureGenerator generator) {
        return generator.generateClosure(depth);
    }

    private void writeClosure(LexicalResourceTranslationClosure<LexicalSense> closure) {
        TranslationClosureWriter writer = new FileTranslationClosureWriter(targetDirectory);
        writer.writeClosure(closure);
    }
}
