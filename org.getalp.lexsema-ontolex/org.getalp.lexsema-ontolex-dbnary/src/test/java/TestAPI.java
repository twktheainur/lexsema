import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceFactory;
import org.getalp.lexsema.ontolex.graph.OWLTBoxModel;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.storage.JenaTDBStore;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;


/**
 * Test class for the DBNary API
 */
@SuppressWarnings("LawOfDemeter")
public class TestAPI {

    private static final String TEST_VOCABLE = "chien";
    private final String DB_PATH = ".." + File.separatorChar + "data" + File.separatorChar + "dbnary" + File.separatorChar + "dbnarutdb";
    private final String ONTOLOGY_PROPERTIES = "data" + File.separatorChar + "ontology.properties";
    OntologyModel tBox;
    private DBNary dbnary;

    @Before
    @SuppressWarnings("all") // Not exported via interface...
    public void setUp() throws Exception {
        //VirtuosoTripleStore.connect("jdbc:virtuoso://kopi.imag.fr:1982","dba","dba");
        Store vts = new JenaTDBStore(DB_PATH);
        StoreHandler.registerStoreInstance(vts);

        tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);
        // Creating DBnary wrapper
        dbnary = (DBNary) LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, Locale.FRENCH);
    }

    @After
    @SuppressWarnings("all") // Not exported via interface...
    public void tearDown() throws Exception {
        StoreHandler.release();
    }

    @Test
    public void testGetVocable() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Logger logger = getLogger("testVocables()");
        Vocable v = null;
        try {
            v = dbnary.getVocable(TEST_VOCABLE);
            logger.info(v.toString());
        } catch (NoSuchVocableException e) {
            logger.error(e.toString());
        }
        assert v != null;
    }


    @Test
    public void testGetLexicalEntriesForVocable() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Logger logger = getLogger("testVocables()");
        Vocable v;
        List<LexicalEntry> les = null;
        try {
            v = dbnary.getVocable(TEST_VOCABLE);
            logger.info(v.toString());
            les = dbnary.getLexicalEntries(v);
            for (LexicalEntry le : les) {
                logger.info(le.toString());
            }
        } catch (NoSuchVocableException e) {
            logger.error(e.toString());
        }
        assert les != null && !les.isEmpty();
    }

    @Test
    public void testGetLexicalEntriesForLemmaPos() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Logger logger = getLogger("testVocables()");
        List<LexicalEntry> les;
        les = dbnary.getLexicalEntries("chien", "lexinfo:noun");
        for (LexicalEntry le : les) {
            logger.info(le.toString());
        }
        assert !les.isEmpty();
    }

    @Test
    public void testListSensesForLexicalEntry() throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Logger logger = getLogger("testVocables()");
        List<LexicalSense> senses = null;
        try {
            OntologyModel tBox = new OWLTBoxModel(ONTOLOGY_PROPERTIES);

            // Creating DBnary wrapper
            LexicalResource lr = LexicalResourceFactory.getLexicalResource(DBNary.class, tBox, Locale.FRENCH);

            LexicalEntry le = (LexicalEntry) dbnary.getLexicalResourceEntityFactory()
                    .getEntity(LexicalEntry.class,
                            "http://kaiko.getalp.org/dbnary/fra/chien__nom__1", null);


            logger.info(le.toString());
            senses = lr.getLexicalSenses(le);
            for (LexicalSense s : senses) {
                logger.info(s.toString());
            }
        } catch (IOException e) {
            logger.error(e.toString());
        }
        assert senses != null && !senses.isEmpty();
    }

    private Logger getLogger(String method) {
        return LoggerFactory.getLogger(TestAPI.class.getName() + "." + method);
    }
}