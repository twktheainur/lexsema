package org.getalp.lexsema.ontolex.factories.entities;

import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;

import java.util.HashMap;
import java.util.Map;

public final class LexicalResourceEntityFactoryImpl implements LexicalResourceEntityFactory {

    private Map<Class<? extends LexicalResourceEntity>, LexicalResourceEntityBuilder<? extends LexicalResourceEntity>> factories;


    public LexicalResourceEntityFactoryImpl() {
        factories = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked") // Java's type system is not strong enough to ensure type safety in this
    // case, the explicit class equality check ensures the assignment is always consistent
    public void registerFactory(Class<? extends LexicalResourceEntity> productType, LexicalResourceEntityBuilder builder) {
        factories.put(productType, builder);
    }


    private LexicalResourceEntityBuilder<? extends LexicalResourceEntity> getFactory(Class<? extends LexicalResourceEntity> productType) {
        return factories.get(productType);
    }

    @SuppressWarnings("LawOfDemeter")
    @Override
    public LexicalResourceEntity getEntity(Class<? extends LexicalResourceEntity> productType, String uri, LexicalResourceEntity parent) {
        LexicalResourceEntityBuilder<? extends LexicalResourceEntity> factory = getFactory(productType);
        return factory.buildEntity(uri, parent);
    }

    @SuppressWarnings("LawOfDemeter")
    @Override
    public LexicalResourceEntity getEntity(Class<? extends LexicalResourceEntity> productType, String uri, LexicalResourceEntity parent, Map<String, String> properties) {
        LexicalResourceEntityBuilder<? extends LexicalResourceEntity> factory = getFactory(productType);
        return factory.buildEntity(uri, parent, properties);
    }

    @Override
    public void setLexicalResource(LexicalResource lexicalResource) {
        for (LexicalResourceEntityBuilder builder : factories.values()) {
            builder.setLexicalResource(lexicalResource);
        }
    }

    @Override
    public LexicalResourceEntityFactory clone() throws CloneNotSupportedException {
        super.clone();
        LexicalResourceEntityFactory clone = new LexicalResourceEntityFactoryImpl();
        for (Class<? extends LexicalResourceEntity> key : factories.keySet()) {
            clone.registerFactory(key, factories.get(key).clone());
        }
        return clone;
    }
}
