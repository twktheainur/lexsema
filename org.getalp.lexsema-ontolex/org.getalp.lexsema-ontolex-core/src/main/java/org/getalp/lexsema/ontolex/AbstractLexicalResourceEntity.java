package org.getalp.lexsema.ontolex;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.defaultimpl.AbstractNode;

/**
 * Generic Implementation of a Lexical Resource entity
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public abstract class AbstractLexicalResourceEntity extends AbstractNode implements LexicalResourceEntity {

    private LexicalResource lexicalResource;
    private OntologyModel model;
    private LexicalResourceEntity parent;

    /**
     * Constructor for an Abstract Lexical Resource Entity
     *
     * @param r   The lexical resource to which the entity belongs
     * @param uri The uri of the entity
     */
    protected AbstractLexicalResourceEntity(LexicalResource r, String uri, LexicalResourceEntity parent) {
        super((r.getURI().charAt(r.getURI().length() - 1) != '/') ? r.getURI() + "/" : r.getURI() + uri);
        lexicalResource = r;
        model = r.getGraph().getModel();
        this.parent = parent;
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
