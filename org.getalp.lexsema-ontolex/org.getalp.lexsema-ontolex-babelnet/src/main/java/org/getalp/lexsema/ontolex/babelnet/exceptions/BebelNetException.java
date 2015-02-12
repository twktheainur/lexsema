package org.getalp.lexsema.ontolex.babelnet.exceptions;

/**
 * An exception pertaining to DBNary
 */
public class BebelNetException extends Exception {
    public BebelNetException(String message) {
        super("DBNary Exception: " + message);
    }
}
