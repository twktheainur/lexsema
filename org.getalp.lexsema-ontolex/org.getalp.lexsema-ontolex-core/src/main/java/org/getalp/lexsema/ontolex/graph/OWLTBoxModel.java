/**
 *
 */
package org.getalp.lexsema.ontolex.graph;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.getalp.lexsema.ontolex.graph.serialization.SerializableOntModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * An OWL graph api model wrapper around Jena
 */
public class OWLTBoxModel implements OntologyModel {
    private SerializableOntModel model;
    private String propPath = String.format("data%sgraphapi.properties", File.separatorChar);
    private Properties properties;

    /**
     * Default constructor
     *
     * @throws IOException When loading the default properties fails (data/graphapi.properties)
     */
    @SuppressWarnings("unused")
    public OWLTBoxModel() throws IOException {
        loadProperties();
        createModel(null);
    }

    /**
     * Build an {@code OWLTBoxModel} based on an existing model <code>m</code>
     *
     * @param model An existing base graphapi model
     * @throws IOException When loading the default properties fails (data/graphapi.properties)
     */
    @SuppressWarnings("unused")
    public OWLTBoxModel(Model model) throws IOException {
        loadProperties();
        createModel(model);
    }

    /**
     * Load an OWLTBoxModel with a custom properties path
     *
     * @param propPath The path to the properties file
     * @throws IOException When the properties file at {@code propPath} cannot be loaded
     */
    public OWLTBoxModel(String propPath) throws IOException {
        this.propPath = propPath;
        loadProperties();
        createModel(null);
    }

    private void createModel(Model model) {
        if (model == null) {
            this.model = (SerializableOntModel) ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        } else {
            this.model = (SerializableOntModel) ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, model);
        }
        if (properties.containsKey("ontologies")) {
            String[] ontologies = properties.getProperty("ontologies").split(",");
            for (String ont : ontologies) {
                this.model.read(ont.trim());
            }
        }
    }

    /**
     * Load the properties from the properties path
     *
     * @throws IOException When the properties cannot be loaded
     */
    protected void loadProperties() throws IOException, java.io.FileNotFoundException {
        properties = new Properties();
        properties.load(new FileInputStream(propPath));
    }


    @Override
    public Node getNode(String uri) {
        return NodeFactory.createURI(model.expandPrefix(uri));
    }
}
