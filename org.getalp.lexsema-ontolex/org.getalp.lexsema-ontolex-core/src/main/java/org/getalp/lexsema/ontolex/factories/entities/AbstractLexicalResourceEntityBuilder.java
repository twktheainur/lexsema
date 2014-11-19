package org.getalp.lexsema.ontolex.factories.entities;

import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.uri.URIParser;

import java.util.Map;

public abstract class AbstractLexicalResourceEntityBuilder<T extends LexicalResourceEntity> implements LexicalResourceEntityBuilder<T> {
    private LexicalResource lexicalResource;
    private Graph graph;
    private URIParser uriParser;
    private Map<String, String> parameters;

    protected AbstractLexicalResourceEntityBuilder() {
    }

    protected LexicalResource getLexicalResource() {
        return lexicalResource;
    }

    @Override
    public void setLexicalResource(LexicalResource lexicalResource) {
        this.lexicalResource = lexicalResource;
        graph = lexicalResource.getGraph();
    }

    protected Graph getGraph() {
        return graph;
    }

    protected String getResourceGraphURI() {
        return lexicalResource.getResourceGraphURI();
    }

    protected void retrieveURIParser(Class<? extends LexicalResourceEntity> entityClass) {
        uriParser = lexicalResource.getURIParser(entityClass);
    }

    protected boolean uriParserAvailable() {
        return uriParser != null;
    }

    protected Map<String, String> parseURI(String uri) {
        if (uriParserAvailable()) {
            return uriParser.parseURI(uri);
        } else {
            return null;
        }
    }

    @Override
    public T buildEntity(String uri, LexicalResourceEntity parent) {
        return buildEntity(uri, parent, null);
    }
}
