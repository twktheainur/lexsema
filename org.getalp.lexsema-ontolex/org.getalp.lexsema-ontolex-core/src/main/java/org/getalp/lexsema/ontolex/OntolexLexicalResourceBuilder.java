package org.getalp.lexsema.ontolex;

import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.factories.entities.*;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceBuilder;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.uri.URIParser;
import org.getalp.lexsema.ontolex.uri.URIParserRegister;
import org.getalp.lexsema.ontolex.uri.URIParserRegisterImpl;

public abstract class OntolexLexicalResourceBuilder implements LexicalResourceBuilder {
    LexicalResourceEntityFactory entityFactory;
    URIParserRegister uriParserRegister;

    protected OntolexLexicalResourceBuilder() {
        entityFactory = new LexicalResourceEntityFactoryImpl();
        uriParserRegister = new URIParserRegisterImpl();
        entityFactory.registerFactory(LexicalEntry.class, new LexicalEntryBuilder());
        entityFactory.registerFactory(LexicalSense.class, new LexicalSenseBuilder());
    }

    @Override
    public LexicalResource build(OntologyModel model, Language language) {
        return build(model, language, null);
    }

    @Override
    public LexicalResource build(OntologyModel model) {
        return build(model, null, null);
    }

    @Override
    public abstract LexicalResource build(OntologyModel model, Language language, String uri);

    @Override
    public LexicalResource build(OntologyModel model, String uri) {
        return build(model, null, uri);
    }


    protected LexicalResourceEntityFactory getEntityFactory() {
        return entityFactory;
    }

    protected URIParserRegister getUriParserRegister() {
        return uriParserRegister;
    }

    protected void registerURIParser(Class<? extends LexicalResourceEntity> lexicalResourceEntityClass, URIParser uriParser) {
        uriParserRegister.registerURIParser(lexicalResourceEntityClass, uriParser);
    }

    protected void registerEntityFactory(Class<? extends LexicalResourceEntity> productType, LexicalResourceEntityBuilder builder) {
        entityFactory.registerFactory(productType, builder);
    }
}
