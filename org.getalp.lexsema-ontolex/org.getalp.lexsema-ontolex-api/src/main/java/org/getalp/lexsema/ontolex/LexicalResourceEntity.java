package org.getalp.lexsema.ontolex;


import com.hp.hpl.jena.graph.Node;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.ontolex.graph.OntologyModel;

import java.io.Serializable;

/**
 * A node that is part of a Lexical Resource, i.e. corresponding to an instance of one of the Lemon classes
 */
public interface LexicalResourceEntity extends Comparable<LexicalResourceEntity>, Serializable {
    /**
     * @return The lexical resource the Entity belongs to
     */
    LexicalResource getLexicalResource();

    /**
     * Returns the ontology model associated with the lexical resource and the entity
     *
     * @return the ontology model associated with the lexical resource and the entity
     */
    OntologyModel getOntologyModel();

    /**
     * Get the graph model node
     *
     * @return The graph model node that corresponds to the entity
     */
    Node getNode();

    /**
     * Returns the parent LexicalResourceEntity of this entity, null if there is none
     *
     * @return Returns the parent LexicalResourceEntity of this entity, null if there is none
     */
    LexicalResourceEntity getParent();

    /**
     * Returns the language of the LexicalResourceEntity, Language.UNSUPPORTED if none
     *
     * @return the language of the lexical resource entity
     */
    Language getLanguage();

    /**
     * Returns the language of the LexicalResourceEntity, Language.UNSUPPORTED if none
     *
     */
    void setLanguage(Language language);
}
