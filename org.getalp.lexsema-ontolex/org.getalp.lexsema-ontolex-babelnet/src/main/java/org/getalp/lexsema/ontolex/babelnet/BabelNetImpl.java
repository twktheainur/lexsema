package org.getalp.lexsema.ontolex.babelnet;

import lombok.ToString;
import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.OntolexLexicalResource;
import org.getalp.lexsema.ontolex.babelnet.queries.RelatedEntitiesForLexicalResourceEntityQueryProcessor;
import org.getalp.lexsema.ontolex.babelnet.queries.TranslationsForLexicalResourceEntityQueryProcessor;
import org.getalp.lexsema.ontolex.babelnet.relations.LexinfoRelationType;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.queries.QueryProcessor;
import org.getalp.lexsema.ontolex.uri.URIParserRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * A handler for the DBNary Lexical Resource
 */
@ToString
public final class BabelNetImpl extends OntolexLexicalResource implements BabelNet {

    private final Language language;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Constructor for DBNary
     *
     * @param model    The ontology model where dbnary is stored
     * @param language The language of the dbnary to access
     */
    @SuppressWarnings("HardcodedFileSeparator")
    public BabelNetImpl(OntologyModel model, Language language, String uri,
                        URIParserRegister uriParserRegister,
                        LexicalResourceEntityFactory entityFactory) {
        super(model, uri, uriParserRegister, entityFactory);
        this.language = language;
    }

    @Override
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity) {
        QueryProcessor<Translation> translationTargets =
                new TranslationsForLexicalResourceEntityQueryProcessor(
                        getGraph(),
                        getLexicalResourceEntityFactory(),
                        sourceEntity,
                        null);
        translationTargets.runQuery();
        return translationTargets.processResults();
    }

    @Override
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity, Language language) {
        QueryProcessor<Translation> translationTargets =
                new TranslationsForLexicalResourceEntityQueryProcessor(
                        getGraph(),
                        getLexicalResourceEntityFactory(),
                        sourceEntity,
                        language);
        translationTargets.runQuery();
        return translationTargets.processResults();
    }

    @Override
    public List<LexicalResourceEntity> getRelatedEntities(LexicalResourceEntity sourceEntity, LexinfoRelationType relationType) {
        QueryProcessor<LexicalResourceEntity> relatedTargetsProcessor =
                new RelatedEntitiesForLexicalResourceEntityQueryProcessor(
                        getGraph(),
                        getLexicalResourceEntityFactory(),
                        sourceEntity,
                        relationType);
        relatedTargetsProcessor.runQuery();
        return relatedTargetsProcessor.processResults();
    }

    @Override
    public List<LexicalResourceEntity> getRelatedEntities(LexicalResourceEntity sourceEntity, List<LexinfoRelationType> relationTypeList) {
        List<LexicalResourceEntity> entities = new ArrayList<>();
        for (LexinfoRelationType rt : relationTypeList) {
            entities.addAll(getRelatedEntities(sourceEntity, rt));
        }
        return entities;
    }

    @Override
    public Language getLanguage() {
        return language;
    }
}
