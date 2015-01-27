package org.getalp.lexsema.ontolex.dbnary.factories.entities;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.dbnary.Translation;
import org.getalp.lexsema.ontolex.dbnary.TranslationImpl;
import org.getalp.lexsema.ontolex.dbnary.queries.TranslationPropertiesQueryProcessor;
import org.getalp.lexsema.ontolex.factories.entities.AbstractLexicalResourceEntityBuilder;
import org.getalp.lexsema.ontolex.queries.QueryProcessor;

import java.util.List;
import java.util.Map;

public class TranslationBuilder extends AbstractLexicalResourceEntityBuilder<Translation> {

    public TranslationBuilder() {
        super();
    }


    @Override
    public Translation buildEntity(String uri, LexicalResourceEntity parent, Map<String, String> parameters) {
        /**
         *  We try to get as much information as possible from the URI if any URI parsers are available for
         *  the current <code>LexicalResource</code>
         */

        Node uriNode = NodeFactory.createURI(uri);
        QueryProcessor<Map<String, String>> queryProcessor = new TranslationPropertiesQueryProcessor(getGraph(),
                uriNode);
        //noinspection LawOfDemeter
        queryProcessor.runQuery();
        //noinspection LawOfDemeter
        List<Map<String, String>> results = queryProcessor.processResults();
        Map<String, String> translationProperties = results.get(0);

        String gloss = translationProperties.get("gloss");
        String writtenForm = translationProperties.get("writtenForm");
        String translationNumberString = translationProperties.get("translationNumber");
        int translationNumber;
        if (translationNumberString == null) {
            translationNumber = -1;
        } else {
            translationNumber = Integer.valueOf(translationNumberString);
        }
        String targetLanguage = translationProperties.get("targetLanguage");

        return new TranslationImpl(getLexicalResource(), uri, parent,
                gloss, translationNumber, writtenForm, targetLanguage);
    }
}
