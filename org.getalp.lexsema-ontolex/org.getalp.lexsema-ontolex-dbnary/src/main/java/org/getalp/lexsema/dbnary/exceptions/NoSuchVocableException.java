package org.getalp.lexsema.dbnary.exceptions;

/**
 * An exception expressing that a given vocable does not exist in a given dbnary instance
 */
public class NoSuchVocableException extends DBNaryException {
    public NoSuchVocableException(String vocable, String language) {
        super("Vocable " + vocable + " does not exist in " + language + " Dbnary");
    }
}
