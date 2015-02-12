package org.getalp.lexsema.ontolex.factories.resource;

import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.graph.OntologyModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class LexicalResourceFactory {
    private LexicalResourceFactory() {
    }

    @SuppressWarnings({"all", "LawOfDemeter"})
    public static LexicalResource getLexicalResource(Class<? extends LexicalResource> resourceType, OntologyModel model, Language language) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException {
        String builderName = String.format("%sBuilder", resourceType.getName());
        Class<? extends LexicalResourceBuilder> builderClass = (Class<? extends LexicalResourceBuilder>) Class.forName(builderName);
        Constructor<? extends LexicalResourceBuilder> builderClassConstructor = builderClass.getConstructor();
        LexicalResourceBuilder builder = builderClassConstructor.newInstance();
        return builder.build(model, language);
    }
}
