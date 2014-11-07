package org.getalp.lexsema.wsd.result;

import org.getalp.lexsema.io.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;

/**
 * Created by tchechem on 9/16/14.
 */
public interface ConfigurationWriter {
    public void write(Document d, Configuration c);
}
