/*
 * FatalException.java
 *
 * 12 Jul 2007
 */

package suncertify;

/**
 * Represents fatal exceptions, i.e. ones that will end the application.
 * 
 * @author Richard Wardle
 */
public final class FatalException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String messageKey;

    /**
     * Creates a new instance of <code>FatalException</code>.
     */
    public FatalException() {
        super();
        this.messageKey = null;
    }

    /**
     * Creates a new instance of <code>FatalException</code>.
     * 
     * @param message
     *                The error message.
     */
    public FatalException(String message) {
        super(message);
        this.messageKey = null;
    }

    /**
     * Creates a new instance of <code>FatalException</code>.
     * 
     * @param cause
     *                The root cause.
     */
    public FatalException(Throwable cause) {
        super(cause);
        this.messageKey = null;
    }

    /**
     * Creates a new instance of <code>FatalException</code>.
     * 
     * @param message
     *                The error message.
     * @param cause
     *                The root cause.
     */
    public FatalException(String message, Throwable cause) {
        super(message, cause);
        this.messageKey = null;
    }

    public FatalException(String message, String messageKey, Throwable cause) {
        super(message, cause);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return this.messageKey;
    }
}
