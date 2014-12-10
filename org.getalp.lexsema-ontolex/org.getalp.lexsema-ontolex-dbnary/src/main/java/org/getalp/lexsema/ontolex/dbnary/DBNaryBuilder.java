package org.getalp.lexsema.ontolex.dbnary;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.OntolexLexicalResourceBuilder;
import org.getalp.lexsema.ontolex.dbnary.factories.entities.DBNaryLexicalSenseBuilder;
import org.getalp.lexsema.ontolex.dbnary.factories.entities.TranslationBuilder;
import org.getalp.lexsema.ontolex.dbnary.factories.entities.VocableBuilder;
import org.getalp.lexsema.ontolex.dbnary.uriparsers.DBNaryLexicalEntryURIParser;
import org.getalp.lexsema.ontolex.dbnary.uriparsers.DBNaryLexicalSenseURIParser;
import org.getalp.lexsema.ontolex.dbnary.uriparsers.DBNaryVocableURIParser;
import org.getalp.lexsema.ontolex.graph.OntologyModel;

import java.util.Locale;

@SuppressWarnings("unused")
public class DBNaryBuilder extends OntolexLexicalResourceBuilder {

    public DBNaryBuilder() {
        super();
        /**
         * Registering specific DBNary URI Parsers
         */
        registerURIParser(LexicalEntry.class, new DBNaryLexicalEntryURIParser());
        registerURIParser(LexicalSense.class, new DBNaryLexicalSenseURIParser());
        registerURIParser(Vocable.class, new DBNaryVocableURIParser());

        /**
         * Registering Vocable factory
         */
        registerEntityFactory(Vocable.class, new VocableBuilder());
        registerEntityFactory(LexicalSense.class, new DBNaryLexicalSenseBuilder());
        registerEntityFactory(Translation.class, new TranslationBuilder());
    }

    @SuppressWarnings("HardcodedFileSeparator")
    @Override
    public LexicalResource build(OntologyModel model, Locale language) {
        String uri = String.format("%s/%s/", model.getNode("dbnary:").getURI().split("#")[0], language.getISO3Language());
        return build(model, language, uri);
    }

    @Override
    public LexicalResource build(OntologyModel model, Locale language, String uri) {
        return new DBNaryImpl(model, language, uri, getUriParserRegister(), getEntityFactory());
    }
}
