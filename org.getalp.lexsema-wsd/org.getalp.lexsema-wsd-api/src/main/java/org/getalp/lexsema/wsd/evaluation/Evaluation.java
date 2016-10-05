package org.getalp.lexsema.wsd.evaluation;

import org.getalp.lexsema.wsd.configuration.Configuration;

public interface Evaluation {
    WSDResult evaluate(GoldStandard goldStandard, Configuration c);
}
