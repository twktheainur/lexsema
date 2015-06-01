import org.getalp.lexsema.io.text.RussianPythonSentenceProcessor;
import org.getalp.lexsema.io.text.SentenceProcessor;
import org.getalp.lexsema.similarity.Sentence;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests sentence processor
 */
public class TestSentenceProcessor {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Test
    public void testRussianSentenceProcessor() throws Exception {
        SentenceProcessor sp = new RussianPythonSentenceProcessor();
        Sentence s = sp.process("Как вам зовуть?", "");
        logger.info(s.toString());
    }
}
