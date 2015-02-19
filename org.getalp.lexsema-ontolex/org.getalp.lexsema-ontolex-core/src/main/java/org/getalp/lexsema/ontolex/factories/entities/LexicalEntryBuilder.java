package org.getalp.lexsema.ontolex.factories.entities;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalEntryImpl;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.queries.QueryProcessor;
import org.getalp.lexsema.ontolex.queries.properties.LexicalEntryPropertiesQueryProcessor;

import java.util.List;
import java.util.Map;

public class LexicalEntryBuilder extends AbstractLexicalResourceEntityBuilder<LexicalEntry> {

    public LexicalEntryBuilder() {
        super();
    }

    @Override
    public LexicalEntry buildEntity(final String uri, final LexicalResourceEntity parent, final Map<String, String> parameters) {

        String lemma = null;
        String pos = null;
        String lexicalEntryNumber = null;
        retrieveURIParser(LexicalEntry.class);
        String entityURI = processURI(uri);


        Map<String, String> values = parseURI(entityURI);
        if (values != null) {
            lemma = values.get("canonicalFormWrittenRep");
            pos = values.get("partOfSpeech");
            lexicalEntryNumber = values.get("number");
        }

        if (parameters != null) {
            if (lemma == null) {
                lemma = parameters.get("canonicalFormWrittenRep");
            }
            //if (pos == null) {
            //    pos = parameters.get("partOfSpeech");
            //}
            if (lexicalEntryNumber == null) {
                lexicalEntryNumber = parameters.get("number");
            }
        }

        if (lemma == null || pos == null) {
            QueryProcessor<LexicalEntryPropertiesQueryProcessor.LexicalEntryProperties>
                    lexicalEntryPropertiesQueryProcessor =
                    new LexicalEntryPropertiesQueryProcessor(getGraph(), getLexicalResource(), entityURI, lemma, pos);

            lexicalEntryPropertiesQueryProcessor.runQuery();
            List<LexicalEntryPropertiesQueryProcessor.LexicalEntryProperties> instances =
                    lexicalEntryPropertiesQueryProcessor.processResults();
            @SuppressWarnings("LocalVariableOfConcreteClass")
            LexicalEntryPropertiesQueryProcessor.LexicalEntryProperties properties = instances.get(0);
            if (lemma == null) {
                lemma = properties.getLemma();
            }
            if (pos == null) {
                pos = properties.getPos();
            }
        }
        LexicalEntry instance = new LexicalEntryImpl(getLexicalResource(), uri, parent, lemma, pos);
        if (lexicalEntryNumber != null) {
            instance.setNumber(Integer.valueOf(lexicalEntryNumber));
        }
        return instance;
    }

    @Override
    public LexicalResourceEntityBuilder<LexicalEntry> clone() throws CloneNotSupportedException {
        super.clone();
        LexicalResourceEntityBuilder<LexicalEntry> clone = new LexicalEntryBuilder();
        clone.setLexicalResource(getLexicalResource());
        return clone;
    }

    private String processURI(String uri) {
        //noinspection HardcodedFileSeparator
        if (uri.contains("/")) {
            return uri;
        } else {
            return getResourceGraphURI() + uri;
        }
    }
}
