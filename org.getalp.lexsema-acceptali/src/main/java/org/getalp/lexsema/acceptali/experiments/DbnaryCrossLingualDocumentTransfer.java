package org.getalp.lexsema.acceptali.experiments;

import org.getalp.lexsema.acceptali.crosslingual.translation.DbNaryTranslator;
import org.getalp.lexsema.acceptali.crosslingual.translation.Translator;
import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaRemoteSPARQLStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

import static java.io.File.separator;


/**
 * TODO: Add stop word filtering pre-translation
 * TODO: Add lemmatization and pos tagging
 */

public final class DbnaryCrossLingualDocumentTransfer {

    public static final String ONTOLOGY_PROPERTIES = String.format("data%sontology.properties", separator);
    static Language[] loadLanguages = {
            Language.FRENCH, Language.ENGLISH,
            Language.RUSSIAN
    };
    private static Logger logger = LoggerFactory.getLogger(DbnaryCrossLingualDocumentTransfer.class);


    private DbnaryCrossLingualDocumentTransfer() {
    }


    public static void main(String[] args) throws IOException, NoSuchVocableException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String sourceLang = args[0];
        String targetLang = args[1];
        String sourceDir = args[2];
        String targetDir = args[3];

        Language lang1 = Language.fromCode(sourceLang);
        Language lang2 = Language.fromCode(targetLang);

        Translator translator = new DbNaryTranslator(instantiateDBNary());

        File sourceDirectoryFile = new File(sourceDir);
        File targetDirectoryFile = new File(targetDir);
        if (!targetDirectoryFile.exists() && targetDirectoryFile.mkdirs()) {

            for (File child : sourceDirectoryFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !name.equals(".DS_Store");
                }
            })) {
                try (BufferedReader br = new BufferedReader(new FileReader(child))) {
                    String text = "";
                    String line = br.readLine();
                    while (line != null) {
                        text += line + " ";
                        line = br.readLine();
                    }
                    String translated = translator.translate(text, lang1, lang2);
                    File transferredFile = new File(targetDirectoryFile, child.getName());
                    @SuppressWarnings("IOResourceOpenedButNotSafelyClosed") PrintWriter pw = new PrintWriter(transferredFile);
                    pw.println(translated);
                    pw.flush();
                    pw.close();
                }
            }
        } else {
            logger.error("The specified target directory does not exist and could not be created.");
        }
    }


    private static DBNary instantiateDBNary() throws IOException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Store vts = new JenaRemoteSPARQLStore("http://kaiko.getalp.org/sparql");
        StoreHandler.registerStoreInstance(vts);
        //StoreHandler.DEBUG_ON = true;
        OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        // Creating DBNary wrapper
        return (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, loadLanguages);
    }

}
