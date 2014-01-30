package org.getalp.lexsema.lexicalresource.lemon;

import org.getalp.lexsema.lexicalresource.AbstractLexicalResource;
import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.uri.LemonURICollection;
import org.getalp.lexsema.ontology.uri.URICollection;

import java.util.List;

/**
 * A Lemon Lexical Resource Handler
 */
public abstract class LemonLexicalResource extends AbstractLexicalResource implements LemonEntityFactory {

    private URICollection lemonUri;
    private String uri;

    {
        lemonUri = new LemonURICollection(getModel());
    }

    /**
     * Default constructor
     *
     * @param model The underlying ontology model
     */
    protected LemonLexicalResource(OntologyModel model, String uri) {
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
