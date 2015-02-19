package org.getalp.lexsema.ontolex.factories.entities;

import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;

import java.util.Map;

/**
 * Interface for a generic builder (factory worker) that knows how to instantiate on particular type of
 * <code>LexicalResourceEntity</code>. <code>LexicalResourceEntityBuilder</code>s are meant to be registered with
 * <code>LexicalResourceEntityFactory</code>.
 *
 * @param <T>
 */
public interface LexicalResourceEntityBuilder<T extends LexicalResourceEntity> extends Cloneable {
    /**
     * Builds an instance of the <code>LexicalResourceEntity</code> with URI <code>uri</code> and parent <code>parent</code>
     *
     * @param uri    The URI of the entity.
     * @param parent The parent of the entity, may be null if the entity is top-level.
     * @return The instance of the entity.
     */
    public T buildEntity(String uri, LexicalResourceEntity parent);

    /**
     * Builds an instance of the <code>LexicalResourceEntity</code> with URI <code>uri</code> and parent <code>parent</code>
     *
     * @param uri    The URI of the entity.
     * @param parent The parent of the entity, may be null if the entity is top-level.
     * @return The instance of the entity.
     */
    public T buildEntity(String uri, LexicalResourceEntity parent, Map<String, String> parameters);

    /**
     * Sets the lexical resource to which the entity belongs to.
     *
     * @param lexicalResource The <code>LexicalResource</code> instance.
     */
    public void setLexicalResource(LexicalResource lexicalResource);

    public LexicalResourceEntityBuilder<T> clone() throws CloneNotSupportedException;
}
