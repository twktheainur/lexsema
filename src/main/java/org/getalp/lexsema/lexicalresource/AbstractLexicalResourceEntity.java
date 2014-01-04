package org.getalp.lexsema.lexicalresource;

import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.graph.defaultimpl.AbstractNode;

/**
 * Generic Implementation of a Lexical Resource entity
 */
public abstract class AbstractLexicalResourceEntity extends AbstractNode implements LexicalResourceEntity {

    private LexicalResource lexicalResource;
    private OntologyModel model;

    /**
     * Constructor for an Abstract Lexical Resource Entity
     *
     * @param r   The lexical resource to which the entity belongs
     * @param uri The uri of the entity
     */
    protected AbstractLexicalResourceEntity(LexicalResource r, String uri) {
        super(r.getURI() + uri, r);
        lexicalResource = r;
        model = r.getGraph().getModel();
    }

    @Override
    public LexicalResource getLexicalResource() {
        return lexicalResource;
    }

    @Override
    public OntologyModel getOntologyModel() {
        return model;
    }
}
