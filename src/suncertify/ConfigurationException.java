/*
 * ConfigurationException.java
 *
 * 05 Jun 2007
 */

package suncertify;

/**
 * Represents configuration exception.
 * 
 * @author Richard Wardle
 */
public final class ConfigurationException extends Exception {

    // TODO Remove this and use ApplicationException?

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>ConfigurationException</code>.
     */
    public ConfigurationException() {
        super();
    }

    /**
     * Creates a new instance of <code>ConfigurationException</code>.
     * 
     * @param message
     *                The error message.
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of <code>ConfigurationException</code>.
     * 
     * @param cause
     *                The root cause.
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of <code>ConfigurationException</code>.
     * 
     * @param message
     *                The error message.
     * @param cause
     *                The root cause.
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
