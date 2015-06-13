import org.getalp.lexsema.io.text.RussianPythonTextProcessor;
import org.getalp.lexsema.io.text.TextProcessor;
import org.getalp.lexsema.similarity.Text;
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
        TextProcessor sp = new RussianPythonTextProcessor();
        Text s = sp.process("Как вам зовуть?", "");
        logger.info(s.toString());
    }
}
