/*
 * DuplicateKeyException.java
 *
 * Created on 07-Jul-2005
 */

package suncertify.db;

/**
 * TODO
 * 
 * @author Richard Wardle
 */
public final class DuplicateKeyException extends Exception {

    /**
     * Creates a new <code>DuplicateKeyException</code>.
     */
    public DuplicateKeyException() {
        super();
    }

    /**
     * Creates a new <code>DuplicateKeyException</code> with the specified
     * detail message.
     * 
     * @param message
     *            The detail message.
     */
    public DuplicateKeyException(String message) {
        super(message);
    }

    /**
     * Creates a new <code>DuplicateKeyException</code> with the specified
     * cause.
     * 
     * @param cause
     *            The cause.
     */
    public DuplicateKeyException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new <code>DuplicateKeyException</code> with the specified
     * message and cause.
     * 
     * @param message
     *            The message.
     * @param cause
     *            The cause.
     */
    public DuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
