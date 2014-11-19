package org.getalp.lexsema.ontolex.exceptions;

/**
 * Exception thrown when the initializer methods of a singleton has not been called before getInstance()
 */
@SuppressWarnings("unused")
class NotInstantiatedException extends Exception {
    public NotInstantiatedException() {
        super("The singleton needs to be initialize(...)'ed first");
    }
}
