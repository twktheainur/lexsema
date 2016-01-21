package org.getalp.lexsema.ontolex;

import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.ontolex.exceptions.NotRegisteredException;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.graph.DefaultGraph;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.queries.LexicalEntriesFromLemmaPosQueryProcessor;
import org.getalp.lexsema.ontolex.queries.LexicalSensesOfLexicalEntryQueryProcessor;
import org.getalp.lexsema.ontolex.queries.QueryProcessor;
import org.getalp.lexsema.ontolex.uri.URIParser;
import org.getalp.lexsema.ontolex.uri.URIParserRegister;

import java.util.List;


/**
 * Operations and  attributes common to all classes implementing {@code LexicalResource}.
 * - {@code getGraph}
 * - {@code getFactory}
 * - {@code getURIParser}
 * - {@code getURIModel}
 */
public abstract class OntolexLexicalResource implements LexicalResource {

    private Graph graph;
    private final OntologyModel model;
    private final LexicalResourceEntityFactory lexicalResourceEntityFactory;
    @SuppressWarnings("all")
    private URIParserRegister uriParserRegister;
    private final String uri;


    protected OntolexLexicalResource(OntologyModel model, String uri, URIParserRegister uriParserRegister, LexicalResourceEntityFactory lexicalResourceEntityFactory) {
        this.model = model;
        this.uriParserRegister = uriParserRegister;
        this.lexicalResourceEntityFactory = lexicalResourceEntityFactory;
        this.uri = uri;
    }

    @Override
    public Graph getGraph() {
        if (graph == null) {
            graph = new DefaultGraph(getResourceGraphURI(), model);
        }
        return graph;
    }

    @Override
    public Graph getGraph(Language language) {
        return getGraph();
    }

    @Override
    public String getResourceGraphURI() {
        return uri;
    }

    @Override
    public String getResourceGraphURI(Language language) {
        return getResourceGraphURI();
    }

    @Override
    public URIParser getURIParser(Class<? extends LexicalResourceEntity> entityClass) {
        try {
            return uriParserRegister.getFactory(entityClass);
        } catch (NotRegisteredException ignored) {
            return null;
        }
    }

    @Override
    public OntologyModel getModel() {
        return model;
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry) {
        return null; //TODO: WRITE THIS API METHOD IMPL
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry, String pos) {
        QueryProcessor<LexicalEntry> getLexicalEntries = new LexicalEntriesFromLemmaPosQueryProcessor(getGraph(), getLanguage(), getLexicalResourceEntityFactory(), entry, pos);
        getLexicalEntries.runQuery();
        return getLexicalEntries.processResults();
    }

    @Override
    public List<LexicalSense> getLexicalSenses(LexicalEntry lexicalEntry) {
        QueryProcessor<LexicalSense> getLexicalSensesQuery =
                new LexicalSensesOfLexicalEntryQueryProcessor(getGraph(), getLexicalResourceEntityFactory(), lexicalEntry);
        getLexicalSensesQuery.runQuery();
        return getLexicalSensesQuery.processResults();
    }

    protected String getURI() {
        return uri;
    }

    @Override
    public LexicalResourceEntityFactory getLexicalResourceEntityFactory() {
        return lexicalResourceEntityFactory;
    }

    protected void registerSelfToLexicalEntityResourceFactory() {
        lexicalResourceEntityFactory.setLexicalResource(this);
    }
}
