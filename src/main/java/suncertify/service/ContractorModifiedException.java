/*
 * ContractorModifiedException.java
 *
 * 17 Oct 2007
 */

package suncertify.service;

/**
 * Exception indicating that a contractor has been modified.
 *
 * @author Richard Wardle
 */
public final class ContractorModifiedException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>ContractorModifiedException</code> with <code>message</code>
     * and <code>cause</code> initialised to <code>null</code>.
     */
    public ContractorModifiedException() {
        super();
    }

    /**
     * Creates a new instance of <code>ContractorModifiedException</code> with the specified
     * <code>message</code>, and with <code>cause</code> initialised to <code>null</code>.
     *
     * @param message Error message.
     */
    public ContractorModifiedException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of <code>ContractorModifiedException</code> with the specified
     * <code>cause</code>, and with <code>message</code> initialised to <code>null</code>.
     *
     * @param cause Root cause.
     */
    public ContractorModifiedException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of <code>ContractorModifiedException</code> with the specified
     * <code>message</code> and <code>cause</code>.
     *
     * @param message Error message.
     * @param cause   Root cause.
     */
    public ContractorModifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}
