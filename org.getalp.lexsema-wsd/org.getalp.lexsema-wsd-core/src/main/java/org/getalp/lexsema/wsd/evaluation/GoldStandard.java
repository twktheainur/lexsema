package org.getalp.lexsema.wsd.evaluation;


import org.getalp.lexsema.wsd.configuration.Configuration;

import java.util.List;

public interface GoldStandard {
    public List<Integer> matches(Configuration c);
}
