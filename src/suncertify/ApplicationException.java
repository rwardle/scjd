/*
 * ApplicationException.java
 *
 * Created on 12-Jul-2007
 */

package suncertify;

/**
 * Represents generic application exceptions.
 *
 * @author Richard Wardle
 */
public final class ApplicationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>ApplicationException</code>.
     */
    public ApplicationException() {
        super();
    }

    /**
     * Creates a new instance of <code>ApplicationException</code>.
     *
     * @param message The error message.
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of <code>ApplicationException</code>.
     *
     * @param cause The root cause.
     */
    public ApplicationException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of <code>ApplicationException</code>.
     *
     * @param message The error message.
     * @param cause The root cause.
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
