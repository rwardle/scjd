/*
 * RecordNotFoundException.java
 *
 * Created on 07-Jul-2005
 */

package suncertify.db;

/**
 * Exception thrown if a record does not exist or has been deleted in the
 * database.
 * 
 * @author Richard Wardle
 */
public final class RecordNotFoundException extends Exception {

    /**
     * Creates a new <code>RecordNotFoundException</code>.
     */
    public RecordNotFoundException() {
        super();
    }

    /**
     * Creates a new <code>RecordNotFoundException</code> with the specified
     * detail message.
     * 
     * @param message
     *            The detail message.
     */
    public RecordNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new <code>RecordNotFoundException</code> with the specified
     * cause.
     * 
     * @param cause
     *            The cause.
     */
    public RecordNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new <code>RecordNotFoundException</code> with the specified
     * message and cause.
     * 
     * @param message
     *            The message.
     * @param cause
     *            The cause.
     */
    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
