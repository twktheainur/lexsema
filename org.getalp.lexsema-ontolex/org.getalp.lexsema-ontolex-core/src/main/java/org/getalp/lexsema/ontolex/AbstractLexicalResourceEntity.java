package org.getalp.lexsema.ontolex;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import org.getalp.lexsema.ontolex.graph.OntologyModel;

/**
 * Generic Implementation of a Lexical Resource entity
 */
public abstract class AbstractLexicalResourceEntity implements LexicalResourceEntity {

    private LexicalResource lexicalResource;
    private OntologyModel model;
    private LexicalResourceEntity parent;
    private Node node;


    /**
     * Constructor for an Abstract Lexical Resource Entity
     *
     * @param r   The lexical resource to which the entity belongs
     * @param uri The uri of the entity
     */
    @SuppressWarnings("HardcodedFileSeparator")
    protected AbstractLexicalResourceEntity(LexicalResource r, String uri, LexicalResourceEntity parent) {
        lexicalResource = r;
        model = r.getModel();
        this.parent = parent;
        String baseURI = r.getResourceGraphURI();
        String newUri = "";
        if (!uri.contains("/")) {
            newUri = baseURI;
            if (!baseURI.endsWith("/")) {
                newUri += "/";
            }
        }
        newUri += uri;
        node = NodeFactory.createURI(newUri);
    }

    @Override
    public LexicalResource getLexicalResource() {
        return lexicalResource;
    }

    @Override
    public OntologyModel getOntologyModel() {
        return model;
    }

    @Override
    public Node getNode() {
        return node;
    }
}
