package org.getalp.lexsema.io.thesaurus;


import java.util.List;

public interface AnnotatedTextThesaurus {
    
    public List<String> getRelatedWords(String semanticTag);
}
