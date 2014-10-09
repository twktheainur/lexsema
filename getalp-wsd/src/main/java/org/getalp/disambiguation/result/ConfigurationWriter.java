package org.getalp.disambiguation.result;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;

/**
 * Created by tchechem on 9/16/14.
 */
public interface ConfigurationWriter {
    public void write(Document d, Configuration c);
}
