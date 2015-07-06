package org.getalp.lexsema.wsd.method.aca.model;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.nd4j.linalg.api.ndarray.INDArray;

public class EnvironmentImpl implements Environment {
    Document document;
    Configuration configuration;
    INDArray environmentData;

    public EnvironmentImpl(Document document, Configuration configuration, INDArray environmentData) {
        this.document = document;
        this.configuration = configuration;
        this.environmentData = environmentData;
    }
}
