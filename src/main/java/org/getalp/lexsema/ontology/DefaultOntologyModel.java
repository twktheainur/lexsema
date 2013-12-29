/**
 *
 */
package org.getalp.lexsema.ontology;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author tchechem
 */
public class DefaultOntologyModel implements OntologyModel {
    private OntModel model;
    private String propPath = "data" + File.pathSeparatorChar + "ontology.properties";
    private Properties properties;


    public DefaultOntologyModel() throws IOException {
        loadProperties();
        createModel(null);
    }

    public DefaultOntologyModel(Model m) throws IOException {
        loadProperties();
        createModel(m);
    }

    public DefaultOntologyModel(String propPath) throws IOException {
        this.propPath = propPath;
        loadProperties();
        createModel(null);

    }

    private void createModel(Model m) {
        if (m == null) {
            model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        } else {
            model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, m);
        }
        if (properties.containsKey("ontologies")) {
            String[] ontologies = properties.getProperty("ontologies").split(",");
            for (String ont : ontologies) {
                model.read(ont.trim());
            }
        }
    }

    @Override
    public void loadProperties() throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream(propPath));
    }

    @Override
    public OntModel getJenaModel() {
        return model;
    }


    @Override
    public Node getUri(String element) {
        return NodeFactory.createURI(getJenaModel().expandPrefix(element));
    }
}
