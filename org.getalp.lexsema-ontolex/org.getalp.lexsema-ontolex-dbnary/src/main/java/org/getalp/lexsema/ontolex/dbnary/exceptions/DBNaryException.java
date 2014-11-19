package org.getalp.lexsema.ontolex.dbnary.exceptions;

/**
 * An exception pertaining to DBNary
 */
public class DBNaryException extends Exception {
    public DBNaryException(String message) {
        super("DBNary Exception: " + message);
    }
}
