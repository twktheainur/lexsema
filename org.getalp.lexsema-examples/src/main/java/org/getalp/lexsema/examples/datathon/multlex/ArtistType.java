package org.getalp.lexsema.examples.datathon.multlex;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.getalp.lexsema.examples.datathon.multlex.lexicalization.LexicalizationAlternatives;
import org.getalp.lexsema.examples.datathon.multlex.lexicalization.LexicalizationAlternativesImpl;
import org.getalp.lexsema.util.Language;


public class ArtistType {
    RDFNode uri;
    String writtenForm;
    LexicalizationAlternatives lexicalizationAlternatives = new LexicalizationAlternativesImpl();


    public ArtistType(RDFNode uri, String writtenForm) {
        this.uri = uri;
        this.writtenForm = writtenForm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistType that = (ArtistType) o;
        return uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }


    public void addLexicalization(Language language, String translation){
        lexicalizationAlternatives.registerLexicalization(language,translation);
    }

    public LexicalizationAlternatives getLexicalizationAlternatives() {
        return lexicalizationAlternatives;
    }

    public String getWrittenForm() {
        return writtenForm;
    }
}
