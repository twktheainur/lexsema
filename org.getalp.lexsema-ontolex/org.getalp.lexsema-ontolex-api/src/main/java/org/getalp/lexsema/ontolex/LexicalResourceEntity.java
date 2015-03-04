package org.getalp.lexsema.ontolex;


import com.hp.hpl.jena.graph.Node;
import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.graph.OntologyModel;

/**
 * A node that is part of a Lexical Resource, i.e. corresponding to an instance of one of the Lemon classes
 */
public interface LexicalResourceEntity extends Comparable<LexicalResourceEntity> {
    /**
     * @return The lexical resource the Entity belongs to
     */
    public LexicalResource getLexicalResource();

    /**
     * Returns the ontology model associated with the lexical resource and the entity
     *
     * @return the ontology model associated with the lexical resource and the entity
     */
    public OntologyModel getOntologyModel();

    /**
     * Get the graph model node
     *
     * @return The graph model node that corresponds to the entity
     */
    public Node getNode();

    /**
     * Returns the parent LexicalResourceEntity of this entity, null if there is none
     *
     * @return Returns the parent LexicalResourceEntity of this entity, null if there is none
     */
    public LexicalResourceEntity getParent();

    /**
     * Returns the language of the LexicalResourceEntity, Language.UNSUPPORTED if none
     *
     * @return the language of the lexical resource entity
     */
    public Language getLanguage();

    /**
     * Returns the language of the LexicalResourceEntity, Language.UNSUPPORTED if none
     *
     * @return the language of the lexical resource entity
     */
    public void setLanguage(Language language);
}
