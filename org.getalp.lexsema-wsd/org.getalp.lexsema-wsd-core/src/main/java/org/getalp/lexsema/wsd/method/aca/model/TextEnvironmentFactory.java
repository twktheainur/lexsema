package org.getalp.lexsema.wsd.method.aca.model;


import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class TextEnvironmentFactory implements EnvironmentFactory{

    private Text text;

    public TextEnvironmentFactory(Text text) {
        this.text = text;
    }

    @Override
    public Environment build() {
        Configuration configuration = new ConfidenceConfiguration(text);
        INDArray data = Nd4j.create(10, 10, 3);


        Environment environment = new EnvironmentImpl(text, configuration,data);
        return null;
    }
}
