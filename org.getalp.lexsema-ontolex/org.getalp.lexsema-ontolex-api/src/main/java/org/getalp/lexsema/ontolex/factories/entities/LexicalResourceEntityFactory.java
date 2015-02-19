package org.getalp.lexsema.ontolex.factories.entities;

import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;

import java.util.Map;

/**
 * A generic DPI factory for <code>LexicalResourceEntity</code> instances. Builders must be registered by
 * each <code>LexicalResource</code> implementation.
 */
public interface LexicalResourceEntityFactory extends Cloneable {
    /**
     * Register a <code>LexicalResourceEntityBuilder</code> <code>builder<code/> for <code>productType</code>
     *
     * @param productType The <code>LexicalResourceEntity</code> class type to register the builder for
     * @param builder     The corresponding <code>LexicalResourceEntityBuilder</code> to register
     */
    public void registerFactory(Class<? extends LexicalResourceEntity> productType, LexicalResourceEntityBuilder builder);

    /**
     * Obtain an instance of <code>LexicalResourceEntity</code> of type <code>productType</code> with <code>uri</code> as
     * a URI and <code>parent</code> as a parent entity. <code>parent</code> may be <b>null</b>.
     *
     * @param productType The type of <code>LexicalResourceEntity</code> to build
     * @param uri         The URI of the <code>LexicalResourceEntity</code> to create
     * @param parent      The parent <code>LexicalResourceEntity</code>, may be null.
     * @return The instance
     */
    public LexicalResourceEntity getEntity(Class<? extends LexicalResourceEntity> productType, String uri, LexicalResourceEntity parent);

    /**
     * Obtain an instance of <code>LexicalResourceEntity</code> of type <code>productType</code> with <code>uri</code> as
     * a URI and <code>parent</code> as a parent entity. <code>parent</code> may be <b>null</b>.
     *
     * @param productType The type of <code>LexicalResourceEntity</code> to build
     * @param uri         The URI of the <code>LexicalResourceEntity</code> to create
     * @param parent      The parent <code>LexicalResourceEntity</code>, may be null.
     * @return The instance
     */
    public LexicalResourceEntity getEntity(Class<? extends LexicalResourceEntity> productType, String uri, LexicalResourceEntity parent, Map<String, String> properties);

    /**
     * Sets the lexical resource for which to build entities.
     *
     * @param lexicalResource The <code>LexicalResource</code> instance.
     */
    public void setLexicalResource(LexicalResource lexicalResource);

    public LexicalResourceEntityFactory clone() throws CloneNotSupportedException;
}
