package org.getalp.lexsema.ontolex;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import org.getalp.lexsema.ontolex.graph.OntologyModel;

/**
 * Generic Implementation of a Lexical Resource entity
 */
public abstract class AbstractLexicalResourceEntity implements LexicalResourceEntity {

    private final LexicalResource lexicalResource;
    private final OntologyModel model;
    private final LexicalResourceEntity parent;
    private final String nodeURI;


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
        nodeURI = newUri;
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
        return NodeFactory.createURI(nodeURI);
    }

    @SuppressWarnings("all")
    @Override
    public int compareTo(LexicalResourceEntity o) {
        return getNode().toString().compareTo(o.getNode().toString());
    }

    @Override
    public LexicalResourceEntity getParent() {
        return parent;
    }

    @SuppressWarnings("ALL")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractLexicalResourceEntity)) return false;

        AbstractLexicalResourceEntity that = (AbstractLexicalResourceEntity) o;

        if (getLexicalResource() != null ? !getLexicalResource().equals(that.getLexicalResource()) : that.getLexicalResource() != null)
            return false;
        if (getParent() != null ? !getParent().equals(that.getParent()) : that.getParent() != null) return false;
        return !(nodeURI != null ? !nodeURI.equals(that.nodeURI) : that.nodeURI != null);

    }

    @SuppressWarnings("ALL")
    @Override
    public int hashCode() {
        int result = getLexicalResource() != null ? getLexicalResource().hashCode() : 0;
        result = 31 * result + (getParent() != null ? getParent().hashCode() : 0);
        result = 31 * result + (nodeURI != null ? nodeURI.hashCode() : 0);
        return result;
    }
}
