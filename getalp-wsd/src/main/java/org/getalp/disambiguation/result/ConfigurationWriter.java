package org.getalp.disambiguation.result;

import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.io.Document;

/**
 * Created by tchechem on 9/16/14.
 */
public interface ConfigurationWriter {
    public void write(Document d, Configuration c);
}
