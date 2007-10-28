/*
 * ContractorModifiedException.java
 *
 * 17 Oct 2007
 */

package suncertify.service;

/**
 * 
 * 
 * @author Richard Wardle
 */
public final class ContractorModifiedException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>ContractorModifiedException</code>.
     */
    public ContractorModifiedException() {
        super();
    }

    /**
     * Creates a new instance of <code>ContractorModifiedException</code>.
     * 
     * @param message
     *                The error message.
     */
    public ContractorModifiedException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of <code>ContractorModifiedException</code>.
     * 
     * @param cause
     *                The root cause.
     */
    public ContractorModifiedException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of <code>ContractorModifiedException</code>.
     * 
     * @param message
     *                The error message.
     * @param cause
     *                The root cause.
     */
    public ContractorModifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}
