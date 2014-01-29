package org.getalp.lexsema.util.exceptions;

/**
 * Exception thrown when the initializer methods of a singleton has not been called before getInstance()
 */
public class NotRegisteredException extends Exception {
    public NotRegisteredException(Class c) {
        super("No Corresponding Factory Instance has been registered " + c.getName());
    }
}
