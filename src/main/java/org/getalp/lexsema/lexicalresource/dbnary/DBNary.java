package org.getalp.lexsema.lexicalresource.dbnary;

import org.getalp.lexsema.lexicalresource.LexicalResourceBase;
import org.getalp.lexsema.ontology.ClassURICollection;
import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.lemon.LemonURICollection;
import org.getalp.lexsema.ontology.lemon.LexicalEntry;

import java.util.Locale;


public class DBNary extends LexicalResourceBase {

    private Locale language;

    private ClassURICollection uriCollection;

    {
        uriCollection = new LemonURICollection(getModel());
    }

    public DBNary(OntologyModel model, Locale language) {
        super(model);
        this.language = language;
        addParser(LexicalEntry.class, new DBNaryLexicalEntryURI());
    }

    @Override
    public String getURI() {
        String dbnaryPrefix = getGraph().getModel().getUri("dbnary:").getURI();
        return dbnaryPrefix + "/" + language.getISO3Language() + "/";
    }

    @Override
    public ClassURICollection getURICOllection() {
        return uriCollection;
    }


}
