/**
 *
 */
package org.getalp.lexsema.ml.supervised.weka;

/**
 * @author tchechem
 */
public class ClassifierSetUpException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ClassifierSetUpException() {
        super("Cannot initialize classifier");
    }

}
