package org.getalp.lexsema.ontolex.dbnary.factories.entities;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalEntryImpl;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.dbnary.queries.properties.DbnaryLexicalEntryPropertiesQueryProcessor;
import org.getalp.lexsema.ontolex.factories.entities.AbstractLexicalResourceEntityBuilder;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityBuilder;
import org.getalp.lexsema.ontolex.queries.QueryProcessor;

import java.util.List;
import java.util.Map;

public class DBNaryLexicalEntryBuilder extends AbstractLexicalResourceEntityBuilder<LexicalEntry> {

    @Override
    public LexicalEntry buildEntity(final String uri, final LexicalResourceEntity parent, final Map<String, String> parameters) {


        String lemma = null;
        String pos = null;
        String lexicalEntryNumber = null;
        retrieveURIParser(LexicalEntry.class);
        String entityURI = processURI(uri);

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
            QueryProcessor<DbnaryLexicalEntryPropertiesQueryProcessor.LexicalEntryProperties>
                    lexicalEntryPropertiesQueryProcessor =
                    new DbnaryLexicalEntryPropertiesQueryProcessor(getGraph(), getLexicalResource(), entityURI, lemma, pos);

            lexicalEntryPropertiesQueryProcessor.runQuery();
            List<DbnaryLexicalEntryPropertiesQueryProcessor.LexicalEntryProperties> instances =
                    lexicalEntryPropertiesQueryProcessor.processResults();
            @SuppressWarnings("LocalVariableOfConcreteClass")
            DbnaryLexicalEntryPropertiesQueryProcessor.LexicalEntryProperties properties = instances.get(0);
            if (lemma == null) {
                lemma = properties.getLemma();
            }
            if (pos == null) {
                pos = properties.getPos();
            }
        }
        Map<String, String> values = parseURI(entityURI);
        if (values != null) {
            if(lemma==null) {
                lemma = values.get("canonicalFormWrittenRep");
            }
            if(pos==null) {
                pos = values.get("partOfSpeech");
            }
            if(lexicalEntryNumber==null) {
                lexicalEntryNumber = values.get("lexicalEntryNumber");
            }
        }

        LexicalEntry instance = new LexicalEntryImpl(getLexicalResource(), uri, parent, lemma, pos);
        if (lexicalEntryNumber != null) {
            instance.setNumber(Integer.valueOf(lexicalEntryNumber));
        }
        return instance;
    }

    private String processURI(String uri) {
        //noinspection HardcodedFileSeparator
        if (uri.contains("/")) {
            return uri;
        } else {
            return getResourceGraphURI() + uri;
        }
    }

    @Override
    public LexicalResourceEntityBuilder<LexicalEntry> clone() throws CloneNotSupportedException {
        super.clone();
        LexicalResourceEntityBuilder<LexicalEntry> clone = new DBNaryLexicalEntryBuilder();
        clone.setLexicalResource(getLexicalResource());
        return clone;
    }
}
