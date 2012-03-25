/*
 * ConfigurationException.java
 *
 * 05 Jun 2007
 */

package suncertify;

/**
 * An exception that arises as a result of configuring the application.
 *
 * @author Richard Wardle
 */
public final class ConfigurationException extends Exception {

    /**
     * Creates a new instance of <code>ConfigurationException</code> with
     * <code>message</code> and <code>cause</code> initialised to
     * <code>null</code>.
     */
    public ConfigurationException() {
        super();
    }

    /**
     * Creates a new instance of <code>ConfigurationException</code> with the
     * specified <code>message</code>, and with <code>cause</code>
     * initialised to <code>null</code>.
     *
     * @param message Error message.
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of <code>ConfigurationException</code> with the
     * specified <code>cause</code>, and with <code>message</code>
     * initialised to <code>null</code>.
     *
     * @param cause Root cause.
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of <code>ConfigurationException</code> with the
     * specified <code>message</code> and <code>cause</code>.
     *
     * @param message Error message.
     * @param cause   Root cause.
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
