package org.getalp.lexsema.ontolex.babelnet;

import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.OntolexLexicalResourceBuilder;
import org.getalp.lexsema.ontolex.babelnet.factories.entities.BabelNetLexicalSenseBuilder;
import org.getalp.lexsema.ontolex.babelnet.factories.entities.TranslationBuilder;
import org.getalp.lexsema.ontolex.graph.OntologyModel;

@SuppressWarnings("unused")
public class BabelNetBuilder extends OntolexLexicalResourceBuilder {

    public BabelNetBuilder() {
        super();

        /**
         * Registering Vocable factory
         */
        registerEntityFactory(LexicalSense.class, new BabelNetLexicalSenseBuilder());
        registerEntityFactory(Translation.class, new TranslationBuilder());
    }

    @SuppressWarnings("HardcodedFileSeparator")
    @Override
    public LexicalResource build(OntologyModel model, Language language) {
        String uri = String.format("%s/", "http://babelnet.org/rdf/");
        return build(model, language, uri);
    }

    @Override
    public LexicalResource build(OntologyModel model, Language language, String uri) {
        return new BabelNetImpl(model, language, uri, getUriParserRegister(), getEntityFactory());
    }
}
