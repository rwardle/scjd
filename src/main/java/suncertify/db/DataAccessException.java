/*
 * DataAccessException.java
 *
 * 29 Aug 2007 
 */

package suncertify.db;

/**
 * A runtime exception indicating that there was a problem accessing the database file.
 *
 * @author Richard Wardle
 */
public class DataAccessException extends RuntimeException {

    /**
     * Creates a new instance of <code>DataAccessException</code> with <code>message</code> and
     * <code>cause</code> initialised to <code>null</code>.
     */
    public DataAccessException() {
        super();
    }

    /**
     * Creates a new instance of <code>DataAccessException</code> with the specified
     * <code>message</code>, and with <code>cause</code> initialised to <code>null</code>.
     *
     * @param message Error message.
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of <code>DataAccessException</code> with the specified
     * <code>cause</code>, and with <code>message</code> initialised to <code>null</code>.
     *
     * @param cause Root cause.
     */
    public DataAccessException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of <code>DataAccessException</code> with the specified
     * <code>message</code> and <code>cause</code>.
     *
     * @param message Error message.
     * @param cause   Root cause.
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
