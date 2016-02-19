package org.getalp.lexsema.ontolex.dbnary;

import lombok.ToString;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.OntolexLexicalResource;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.dbnary.queries.LexicalEntriesForVocableQueryProcessor;
import org.getalp.lexsema.ontolex.dbnary.queries.RelatedEntitiesForLexicalResourceEntityQueryProcessor;
import org.getalp.lexsema.ontolex.dbnary.queries.RetrieveAllVocablesQueryProcessor;
import org.getalp.lexsema.ontolex.dbnary.queries.TranslationsForLexicalResourceEntityQueryProcessor;
import org.getalp.lexsema.ontolex.dbnary.relations.DBNaryRelationType;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.queries.QueryProcessor;
import org.getalp.lexsema.ontolex.uri.URIParserRegister;

import java.util.ArrayList;
import java.util.List;


/**
 * A handler for the DBNary Lexical Resource
 */
@ToString
public final class DBNaryImpl extends OntolexLexicalResource implements DBNary {

    private final Language language;

    /**
     * Constructor for DBNary
     *
     * @param model    The ontology model where dbnary is stored
     * @param language The language of the dbnary to access
     */
    @SuppressWarnings("HardcodedFileSeparator")
    public DBNaryImpl(OntologyModel model, Language language, String uri,
                      URIParserRegister uriParserRegister,
                      LexicalResourceEntityFactory entityFactory) {
        super(model, uri, uriParserRegister, entityFactory);
        this.language = language;
        registerSelfToLexicalEntityResourceFactory();
    }


    @Override
    public Vocable getVocable(final String vocable) throws NoSuchVocableException {
        String voc = vocable.toLowerCase();
        QueryProcessor<Vocable> existVocableQueryProcessor = new org.getalp.lexsema.ontolex.dbnary.queries.VocableExistsQueryProcessor(this, vocable);
        existVocableQueryProcessor.runQuery();
        List<Vocable> vocables = existVocableQueryProcessor.processResults();
        if (vocables.isEmpty()) {
            throw new NoSuchVocableException(voc, language.getLanguageName());
        }
        return vocables.get(0);
    }

    @Override
    public Vocable getVocable(String vocable, Language language) throws NoSuchVocableException {
        return getVocable(vocable);
    }

    @Override
    public List<Vocable> getVocables() {
        QueryProcessor<Vocable> retrieveAllVocableQP = new RetrieveAllVocablesQueryProcessor(this);
        retrieveAllVocableQP.runQuery();
        return retrieveAllVocableQP.processResults();
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry, String pos, int entryNumber) {
        List<LexicalEntry> entries = getLexicalEntries(entry, pos);
        List<LexicalEntry> output = new ArrayList<>();
        int i;
        for (i = 0; i < entries.size(); ) {
            if (entries.get(i).getNumber() == entryNumber) {
                output.add(entries.get(i));
            }
        }
        if (i == entries.size()) {
            return entries;
        } else {
            return output;
        }
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry, String pos, int entryNumber, Language language) {
        return getLexicalEntries(entry, pos, entryNumber);
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(Vocable vocable) {

        QueryProcessor<LexicalEntry> lexicalEntryQueryProcessor = new LexicalEntriesForVocableQueryProcessor(this, vocable);
        lexicalEntryQueryProcessor.runQuery();
        return lexicalEntryQueryProcessor.processResults();
    }

    @Override
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity, Language language) {
        QueryProcessor<Translation> translationTargets =
                new TranslationsForLexicalResourceEntityQueryProcessor(this, sourceEntity, language);
        translationTargets.runQuery();
        return translationTargets.processResults();
    }

    @Override
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity, Language... languages) {
        QueryProcessor<Translation> translationTargets =
                new TranslationsForLexicalResourceEntityQueryProcessor(this, sourceEntity, languages);
        translationTargets.runQuery();
        return translationTargets.processResults();
    }

    @Override
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity) {
        QueryProcessor<Translation> translationTargets =
                new TranslationsForLexicalResourceEntityQueryProcessor(
                        this, sourceEntity, null, null);
        translationTargets.runQuery();
        return translationTargets.processResults();
    }

    @Override
    public List<LexicalResourceEntity> getRelatedEntities(LexicalResourceEntity sourceEntity, DBNaryRelationType relationType) {
        QueryProcessor<LexicalResourceEntity> relatedTargetsProcessor =
                new RelatedEntitiesForLexicalResourceEntityQueryProcessor(
                        this,
                        sourceEntity,
                        relationType);
        relatedTargetsProcessor.runQuery();
        return relatedTargetsProcessor.processResults();
    }

    @Override
    public List<LexicalResourceEntity> getRelatedEntities(LexicalResourceEntity sourceEntity, List<DBNaryRelationType> relationTypeList) {
        List<LexicalResourceEntity> entities = new ArrayList<>();
        for (DBNaryRelationType rt : relationTypeList) {
            entities.addAll(getRelatedEntities(sourceEntity, rt));
        }
        return entities;
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry, Language language) {
        return getLexicalEntries(entry);
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry, String pos, Language language) {
        return getLexicalEntries(entry, pos);
    }

    public <T> QueryProcessor<T> getStuff() {
        return null;
    }

    @Override
    public Language getLanguage() {
        return language;
    }
}
