package org.getalp.lexsema.ontolex.factories.entities;

import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;

import java.io.Serializable;
import java.util.Map;

/**
 * A generic DPI factory for {@code LexicalResourceEntity} instances. Builders must be registered by
 * each {@code LexicalResource} implementation.
 */
public interface LexicalResourceEntityFactory extends Cloneable, Serializable {
    /**
     * Register a {@code LexicalResourceEntityBuilder} <code>builder<code/> for {@code productType}
     *
     * @param productType The {@code LexicalResourceEntity} class type to register the builder for
     * @param builder     The corresponding {@code LexicalResourceEntityBuilder} to register
     */
    public void registerFactory(Class<? extends LexicalResourceEntity> productType, LexicalResourceEntityBuilder builder);

    /**
     * Obtain an instance of {@code LexicalResourceEntity} of type {@code productType} with {@code uri} as
     * a URI and {@code parent} as a parent entity. {@code parent} may be <b>null</b>.
     *
     * @param productType The type of {@code LexicalResourceEntity} to build
     * @param uri         The URI of the {@code LexicalResourceEntity} to create
     * @param parent      The parent {@code LexicalResourceEntity}, may be null.
     * @return The instance
     */
    public LexicalResourceEntity getEntity(Class<? extends LexicalResourceEntity> productType, String uri, LexicalResourceEntity parent);

    /**
     * Obtain an instance of {@code LexicalResourceEntity} of type {@code productType} with {@code uri} as
     * a URI and {@code parent} as a parent entity. {@code parent} may be <b>null</b>.
     *
     * @param productType The type of {@code LexicalResourceEntity} to build
     * @param uri         The URI of the {@code LexicalResourceEntity} to create
     * @param parent      The parent {@code LexicalResourceEntity}, may be null.
     * @return The instance
     */
    public LexicalResourceEntity getEntity(Class<? extends LexicalResourceEntity> productType, String uri, LexicalResourceEntity parent, Map<String, String> properties);

    /**
     * Sets the lexical resource for which to build entities.
     *
     * @param lexicalResource The {@code LexicalResource} instance.
     */
    public void setLexicalResource(LexicalResource lexicalResource);

    public LexicalResourceEntityFactory clone() throws CloneNotSupportedException;
}
