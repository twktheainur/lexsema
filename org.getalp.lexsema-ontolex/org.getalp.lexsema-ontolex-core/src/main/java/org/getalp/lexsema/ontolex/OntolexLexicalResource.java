package org.getalp.lexsema.ontolex;


import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.uri.OntolexURICollection;
import org.getalp.lexsema.ontolex.uri.URICollection;

import java.util.List;

/**
 * A Lemon Lexical Resource Handler
 */
public abstract class OntolexLexicalResource extends AbstractLexicalResource implements OntolexEntityFactory {

    private URICollection lemonUri;
    private String uri;

    {
        lemonUri = new OntolexURICollection(getModel());
    }

    /**
     * Default constructor
     *
     * @param model The underlying graphapi model
     */
    protected OntolexLexicalResource(OntologyModel model, String uri) {
        super(model);
        this.uri = uri;
    }

    @Override
    public String getURI() {
        return uri;
    }

    public String getLemonURI(String s) {
        return lemonUri.forName(s);
    }

    @Override
    public String getResourceURI(String s) {
        return getLemonURI(s);
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String entry) {
        return null;
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String lemma, String pos) {
        return null;
    }

    @Override
    public List<LexicalEntry> getLexicalEntries(String lemma, String pos, int entryNumber) {
        return null;
    }

}
