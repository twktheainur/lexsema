package org.getalp.lexsema.ontolex.dbnary;

import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.dbnary.relations.DBNaryRelationType;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.uri.URIParser;
import org.getalp.lexsema.ontolex.uri.URIParserRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Adapter for DBNary with several languages loaded simultaneously
 */
@SuppressWarnings("ClassWithTooManyMethods")
public class MultilingualDBNaryImpl implements DBNary {

    private static Logger logger = LoggerFactory.getLogger(MultilingualDBNaryImpl.class);
    Map<Language, DBNary> resourceMap = new HashMap<>();
    Language defaultLanguage = null;
    OntologyModel model;
    LexicalResourceEntityFactory lexicalResourceEntityFactory;

    public MultilingualDBNaryImpl(OntologyModel model, String uri,
                                  URIParserRegister uriParserRegister,
                                  LexicalResourceEntityFactory entityFactory,
                                  Language... languages) {
        this.model = model;
        lexicalResourceEntityFactory = entityFactory;
        for (Language language : languages) {
            if (defaultLanguage == null) {
                defaultLanguage = language;
            }
            @SuppressWarnings("HardcodedFileSeparator") String languageUri = String.format("%s/%s/", uri, language.getISO3Code());
            try {
                resourceMap.put(language, new DBNaryImpl(model, language, languageUri, uriParserRegister, entityFactory.clone()));
            } catch (CloneNotSupportedException e) {
                logger.info(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public Vocable getVocable(String vocable) throws NoSuchVocableException {
        return resourceMap.get(defaultLanguage).getVocable(vocable);
    }

    @Override
    public Vocable getVocable(String vocable, Language language) throws NoSuchVocableException {
        if (!resourceMap.containsKey(language)) {
            throw new NoSuchVocableException(vocable, "for the languages loaded in");
        }
        return resourceMap.get(language).getVocable(vocable);
    }

    @Override
    public List<Vocable> getVocables() {
        List<Vocable> vocables = new ArrayList<>();
        for(Language language : resourceMap.keySet()) {
            vocables.addAll(resourceMap.get(language).getVocables());
        }
        return vocables;
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry, String pos, int entryNumber) {
        return resourceMap.get(defaultLanguage).getLexicalEntries(entry, pos, entryNumber);
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry, String pos, int entryNumber, Language language) {
        if (!resourceMap.containsKey(language)) {
            return new ArrayList<>();
        }
        return resourceMap.get(language).getLexicalEntries(entry, pos, entryNumber);
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(Vocable vocable) {
        return resourceMap.get(vocable.getLanguage()).getLexicalEntries(vocable);
    }

    @Override
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity, Language language) {
        if (!resourceMap.containsKey(language)) {
            return new ArrayList<>();
        }
        try {
            return resourceMap.get(sourceEntity.getLanguage()).getTranslations(sourceEntity, language);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity, Language... languages) {
        if (!resourceMap.containsKey(sourceEntity.getLanguage())) {
            return new ArrayList<>();
        }
        return resourceMap.get(sourceEntity.getLanguage()).getTranslations(sourceEntity, languages);
    }

    @Override
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity) {
        if (!resourceMap.containsKey(sourceEntity.getLanguage())) {
            return new ArrayList<>();
        }
        return resourceMap.get(sourceEntity.getLanguage()).getTranslations(sourceEntity);
    }

    @Override
    public List<LexicalResourceEntity> getRelatedEntities(LexicalResourceEntity sourceEntity, DBNaryRelationType relationType) {
        if (!resourceMap.containsKey(sourceEntity.getLanguage())) {
            return new ArrayList<>();
        }
        return resourceMap.get(sourceEntity.getLanguage()).getRelatedEntities(sourceEntity, relationType);
    }

    @Override
    public List<LexicalResourceEntity> getRelatedEntities(LexicalResourceEntity sourceEntity, List<DBNaryRelationType> relationTypeList) {
        if (!resourceMap.containsKey(sourceEntity.getLanguage())) {
            return new ArrayList<>();
        }
        return resourceMap.get(sourceEntity.getLanguage()).getRelatedEntities(sourceEntity, relationTypeList);
    }

    @Override
    public String getResourceGraphURI() {
        return resourceMap.get(defaultLanguage).getResourceGraphURI();
    }

    @Override
    public String getResourceGraphURI(Language language) {
        if (!resourceMap.containsKey(language)) {
            return resourceMap.get(defaultLanguage).getResourceGraphURI();
        }
        return resourceMap.get(language).getResourceGraphURI();
    }

    @Override
    public Graph getGraph() {
        return resourceMap.get(defaultLanguage).getGraph();
    }

    @Override
    public Graph getGraph(Language language) {
        if (!resourceMap.containsKey(language)) {
            return resourceMap.get(defaultLanguage).getGraph();
        }
        return resourceMap.get(language).getGraph();
    }

    @Override
    public OntologyModel getModel() {
        return model;
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry) {
        return resourceMap.get(defaultLanguage).getLexicalEntries(entry);
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry, Language language) {
        if (!resourceMap.containsKey(language)) {
            return resourceMap.get(defaultLanguage).getLexicalEntries(entry);
        }
        return resourceMap.get(language).getLexicalEntries(entry);
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry, String pos) {
        return resourceMap.get(defaultLanguage).getLexicalEntries(entry, pos);
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry, String pos, Language language) {
        if (!resourceMap.containsKey(language)) {
            return resourceMap.get(defaultLanguage).getLexicalEntries(entry, pos);
        }
        return resourceMap.get(language).getLexicalEntries(entry, pos);
    }

    @Override
    public List<LexicalSense> getLexicalSenses(LexicalEntry lexicalEntry) {
        if (!resourceMap.containsKey(lexicalEntry.getLanguage())) {
            return resourceMap.get(defaultLanguage).getLexicalSenses(lexicalEntry);
        }
        return resourceMap.get(lexicalEntry.getLanguage()).getLexicalSenses(lexicalEntry);
    }

    @Override
    public URIParser getURIParser(Class<? extends LexicalResourceEntity> entityClass) {
        return resourceMap.get(defaultLanguage).getURIParser(entityClass);
    }

    @Override
    public LexicalResourceEntityFactory getLexicalResourceEntityFactory() {
        return lexicalResourceEntityFactory;
    }

    @Override
    public Language getLanguage() {
        return Language.UNSUPPORTED;
    }
}
