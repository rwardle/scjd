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

    /**
     * Creates a new <code>RecordNotFoundException</code> with
     * <code>message</code> and <code>cause</code> initialised to
     * <code>null</code>.
     */
    public RecordNotFoundException() {
        super();
    }

    /**
     * Creates a new instance of <code>RecordNotFoundException</code> with the
     * specified <code>message</code>, and with <code>cause</code>
     * initialised to <code>null</code>.
     * 
     * @param message
     *                Error message.
     */
    public RecordNotFoundException(String message) {
        super(message);
    }
}
