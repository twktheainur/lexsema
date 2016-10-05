package org.getalp.lexsema.wsd.configuration;

import org.getalp.lexsema.similarity.Document;

import java.io.Serializable;

/**
 * A WSD sense assignment configuration. Allows to assign the index of a sense to a particular word of a Document.
 */
public interface Configuration extends Serializable{
    void setSense(int wordIndex, int senseIndex);

    void setConfidence(int wordIndex, double confidence);

    int getAssignment(int wordIndex);

    double getConfidence(int wordIndex);

    int size();

    int getStart();

    int getEnd();

    void initialize(int value);

    int countUnassigned();

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    int[] getAssignments();

    Document getDocument();
}
