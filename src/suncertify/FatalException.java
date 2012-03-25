/*
 * FatalException.java
 *
 * 12 Jul 2007
 */

package suncertify;

/**
 * An exception that should end the execution of the application. Can be created
 * with a <code>messageKey</code> to use for looking-up a user-friendly error
 * message in a resource bundle.
 *
 * @author Richard Wardle
 */
public final class FatalException extends Exception {

    private final String messageKey;

    /**
     * Creates a new instance of <code>FatalException</code> with
     * <code>message</code>, <code>messageKey</code> and <code>cause</code>
     * initialised to <code>null</code>.
     */
    public FatalException() {
        messageKey = null;
    }

    /**
     * Creates a new instance of <code>FatalException</code> with the
     * specified <code>message</code>, and with <code>messageKey</code> and
     * <code>cause</code> initialised to <code>null</code>.
     *
     * @param message Detail message.
     */
    public FatalException(String message) {
        super(message);
        messageKey = null;
    }

    /**
     * Creates a new instance of <code>FatalException</code> with the
     * specified <code>message</code> and <code>messageKey</code>, and with
     * <code>cause</code> initialised to <code>null</code>.
     *
     * @param message    Detail message.
     * @param messageKey Message key.
     */
    public FatalException(String message, String messageKey) {
        super(message);
        this.messageKey = messageKey;
    }

    /**
     * Creates a new instance of <code>FatalException</code> with the
     * specified <code>message</code>, <code>messageKey</code> and
     * <code>cause</code>.
     *
     * @param message    Detail message.
     * @param messageKey Message key.
     * @param cause      Exception cause.
     */
    public FatalException(String message, String messageKey, Throwable cause) {
        super(message, cause);
        this.messageKey = messageKey;
    }

    /**
     * Returns the message key for use in looking-up a user-friendly error
     * message for this exception.
     *
     * @return The message key.
     */
    public String getMessageKey() {
        return messageKey;
    }
}
