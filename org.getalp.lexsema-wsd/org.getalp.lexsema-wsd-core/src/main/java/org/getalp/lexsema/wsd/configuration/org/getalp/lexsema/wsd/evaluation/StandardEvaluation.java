package org.getalp.lexsema.wsd.configuration.org.getalp.lexsema.wsd.evaluation;

import org.getalp.lexsema.wsd.configuration.Configuration;

import java.util.List;

public class StandardEvaluation implements Evaluation {


    @Override
    public WSDResult evaluate(GoldStandard goldStandard, Configuration c) {

        List<Integer> comparison = goldStandard.matches(c);
        int provided = 0;
        int expected = 0;
        int correct = 0;

        for (Integer i : comparison) {
            if (i == 1) {
                provided++;
                correct++;
            } else if (i == 0) {
                provided++;
            }
            expected++;
        }

        return new WSDResultImpl(provided, expected, correct, c.getDocument());
    }
}
