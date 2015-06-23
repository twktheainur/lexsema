package org.getalp.lexsema.ontolex.dbnary;

import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.OntolexLexicalResourceBuilder;
import org.getalp.lexsema.ontolex.dbnary.factories.entities.DBNaryLexicalEntryBuilder;
import org.getalp.lexsema.ontolex.dbnary.factories.entities.DBNaryLexicalSenseBuilder;
import org.getalp.lexsema.ontolex.dbnary.factories.entities.TranslationBuilder;
import org.getalp.lexsema.ontolex.dbnary.factories.entities.VocableBuilder;
import org.getalp.lexsema.ontolex.dbnary.uriparsers.DBNaryLexicalSenseURIParser;
import org.getalp.lexsema.ontolex.dbnary.uriparsers.DBNaryVocableURIParser;
import org.getalp.lexsema.ontolex.graph.OntologyModel;

@SuppressWarnings({"unused", "OverlyCoupledClass"})
public class DBNaryBuilder extends OntolexLexicalResourceBuilder {

    public DBNaryBuilder() {
        super();
        /**
         * Registering specific DBNary URI Parsers
         */
        //registerURIParser(LexicalEntry.class, new DBNaryLexicalEntryURIParser());
        registerURIParser(LexicalSense.class, new DBNaryLexicalSenseURIParser());
        registerURIParser(Vocable.class, new DBNaryVocableURIParser());

        /**
         * Registering Vocable factory
         */
        registerEntityFactory(Vocable.class, new VocableBuilder());
        registerEntityFactory(LexicalSense.class, new DBNaryLexicalSenseBuilder());
        registerEntityFactory(LexicalEntry.class, new DBNaryLexicalEntryBuilder());
        registerEntityFactory(Translation.class, new TranslationBuilder());
    }

    @SuppressWarnings("HardcodedFileSeparator")
    @Override
    public LexicalResource build(OntologyModel model, Language language) {
        String uri = String.format("%s/%s", model.getNode("dbnary:").getURI().split("#")[0], language.getISO3Code());
        return build(model, language, uri);
    }

    @Override
    public LexicalResource build(OntologyModel model, Language... languages) {
        String uri = String.format("%s", model.getNode("dbnary:").getURI().split("#")[0]);
        return build(model, uri, languages);
    }


    @Override
    public LexicalResource build(OntologyModel model, Language language, String uri) {
        return new DBNaryImpl(model, language, uri, getUriParserRegister(), getEntityFactory());
    }

    @Override
    public LexicalResource build(OntologyModel model, String uri, Language... languages) {
        return new MultilingualDBNaryImpl(model, uri, getUriParserRegister(), getEntityFactory(), languages);
    }
}
