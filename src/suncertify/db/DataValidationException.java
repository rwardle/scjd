/*
 * DataValidationException.java
 *
 * 29 Aug 2007 
 */

package suncertify.db;

/**
 * A database validation exception, i.e. indicates an invalid database file.
 * 
 * @author Richard Wardle
 */
public class DataValidationException extends Exception {

    /**
     * Creates a new instance of <code>DataValidationException</code> with
     * <code>message</code> and <code>cause</code> initialised to
     * <code>null</code>.
     */
    public DataValidationException() {
        super();
    }

    /**
     * Creates a new instance of <code>DataValidationException</code> with the
     * specified <code>message</code>, and with <code>cause</code>
     * initialised to <code>null</code>.
     * 
     * @param message
     *                Error message.
     */
    public DataValidationException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of <code>DataValidationException</code> with the
     * specified <code>cause</code>, and with <code>message</code>
     * initialised to <code>null</code>.
     * 
     * @param cause
     *                Root cause.
     */
    public DataValidationException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of <code>DataValidationException</code> with the
     * specified <code>message</code> and <code>cause</code>.
     * 
     * @param message
     *                Error message.
     * @param cause
     *                Root cause.
     */
    public DataValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
