package org.getalp.lexsema.ontolex.exceptions;

/**
 * Exception thrown when the initializer methods of a singleton has not been called before getInstance()
 */
public class NotInstantiatedException extends Exception {
    public NotInstantiatedException() {
        super("The singleton needs to be initialize(...)'ed first");
    }
}
