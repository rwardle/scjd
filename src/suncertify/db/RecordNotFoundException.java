/*
 * RecordNotFoundException.java
 *
 * 07 Jul 2007
 */

package suncertify.db;

/**
 * Exception thrown if a record does not exist or has been deleted in the
 * database.
 * 
 * @author Richard Wardle
 */
public final class RecordNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

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
     *                The detail message.
     */
    public RecordNotFoundException(String message) {
        super(message);
    }

    // TODO Only add other constructors if needed - semi-specified in SCJD
    // instructions
    /**
     * Creates a new <code>RecordNotFoundException</code> with the specified
     * cause.
     * 
     * @param cause
     *                The cause.
     */
    // public RecordNotFoundException(Throwable cause) {
    // super(cause);
    // }
    /**
     * Creates a new <code>RecordNotFoundException</code> with the specified
     * message and cause.
     * 
     * @param message
     *                The message.
     * @param cause
     *                The cause.
     */
    // public RecordNotFoundException(String message, Throwable cause) {
    // super(message, cause);
    // }
}
