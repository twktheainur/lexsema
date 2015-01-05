package org.getalp.lexsema.supervised.features;

import java.io.IOException;
import java.util.Map;


public interface WindowLoader {
    void load() throws IOException;

    Map<String, WordWindow> getWordWindows();
}
