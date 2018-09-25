package org.getalp.lexsema.ontolex;

import java.util.List;
import java.util.Map;

/**
 * An interface for {@code LexicalSense} instances
 */
public interface LexicalSense extends LexicalResourceEntity {

    String getSenseNumber();

    void setSenseNumber(String senseNumber);

    LexicalConcept getLexicalConcept();

    Map<RelationType, LexicalSense> getRelatedSenses();
    List<LexicalSense> getTranslations();
    Map<RelationType, LexicalSense> getTerminologicallyRelatedSenses();

    boolean isNull();
}
