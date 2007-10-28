/*
 * ContractorDeletedException.java
 *
 * 17 Oct 2007
 */

package suncertify.service;

/**
 * 
 * 
 * @author Richard Wardle
 */
public final class ContractorDeletedException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>ContractorDeletedException</code>.
     */
    public ContractorDeletedException() {
        super();
    }

    /**
     * Creates a new instance of <code>ContractorDeletedException</code>.
     * 
     * @param message
     *                The error message.
     */
    public ContractorDeletedException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of <code>ContractorDeletedException</code>.
     * 
     * @param cause
     *                The root cause.
     */
    public ContractorDeletedException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of <code>ContractorDeletedException</code>.
     * 
     * @param message
     *                The error message.
     * @param cause
     *                The root cause.
     */
    public ContractorDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
