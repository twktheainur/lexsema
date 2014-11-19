package org.getalp.lexsema.ontolex;

import org.getalp.lexsema.ontolex.factories.entities.LexicalEntryBuilder;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityBuilder;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactoryImpl;
import org.getalp.lexsema.ontolex.factories.entities.LexicalSenseBuilder;
import org.getalp.lexsema.ontolex.factories.resource.LexicalResourceBuilder;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.uri.URIParser;
import org.getalp.lexsema.ontolex.uri.URIParserRegister;
import org.getalp.lexsema.ontolex.uri.URIParserRegisterImpl;

import java.util.Locale;

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
    public LexicalResource build(OntologyModel model, Locale language) {
        return build(model, language, null);
    }

    @Override
    public LexicalResource build(OntologyModel model) {
        return build(model, null, null);
    }

    @Override
    public LexicalResource build(OntologyModel model, Locale language, String uri) {
        LexicalResource lr = new OntolexLexicalResource(model, uri, uriParserRegister, entityFactory);
        lr.setURI("");
        return lr;
    }

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
