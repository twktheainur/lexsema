package org.getalp.lexsema.ontolex.dbnary;

import lombok.ToString;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.OntolexLexicalResource;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.dbnary.queries.LexicalEntriesForVocableQueryProcessor;
import org.getalp.lexsema.ontolex.dbnary.queries.RetrieveAllVocablesQueryProcessor;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.queries.QueryProcessor;
import org.getalp.lexsema.ontolex.uri.URIParserRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A handler for the DBNary Lexical Resource
 */
@ToString
public final class DBNaryImpl extends OntolexLexicalResource implements DBNary {

    private final Locale language;
    private final Logger logger = LoggerFactory.getLogger(DBNary.class);

    private QueryProcessor<Vocable> existVocableQueryProcessor;

    /**
     * Constructor for DBNary
     *
     * @param model    The ontology model where dbnary is stored
     * @param language The language of the dbnary to access
     */
    @SuppressWarnings("HardcodedFileSeparator")
    public DBNaryImpl(OntologyModel model, Locale language, String uri,
                      URIParserRegister uriParserRegister,
                      LexicalResourceEntityFactory entityFactory) {
        super(model, uri, uriParserRegister, entityFactory);
        this.language = language;
    }


    @Override
    public Vocable getVocable(final String vocable) throws NoSuchVocableException {
        String voc = vocable.toLowerCase();
        if (existVocableQueryProcessor == null) {
            existVocableQueryProcessor = new org.getalp.lexsema.ontolex.dbnary.queries.VocableExistsQueryProcessor(getGraph(), this, getLexicalResourceEntityFactory(), vocable);
        }
        existVocableQueryProcessor.runQuery();
        List<Vocable> vocables = existVocableQueryProcessor.processResults();

        if (vocables.isEmpty()) {
            throw new NoSuchVocableException(voc, language.getDisplayName());
        }
        return vocables.get(0);
    }

    @Override
    public List<Vocable> getVocables() {
        QueryProcessor<Vocable> retrieveAllVocableQP = new RetrieveAllVocablesQueryProcessor(getGraph(),
                getLexicalResourceEntityFactory());
        retrieveAllVocableQP.runQuery();
        return retrieveAllVocableQP.processResults();
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry, String pos, int entryNumber) {
        List<LexicalEntry> entries = super.getLexicalEntries(entry, pos);
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
    public List<LexicalEntry> getLexicalEntries(Vocable vocable) {

        QueryProcessor<LexicalEntry> lexicalEntryQueryProcessor = new LexicalEntriesForVocableQueryProcessor(getGraph(), getLexicalResourceEntityFactory(), vocable);
        lexicalEntryQueryProcessor.runQuery();
        return lexicalEntryQueryProcessor.processResults();
    }

    @Override
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity, Locale language) {
        return null;
    }

    @Override
    public Locale getLanguage() {
        return language;
    }
}
