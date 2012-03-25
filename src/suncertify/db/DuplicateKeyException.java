/*
 * DuplicateKeyException.java
 *
 * 07 Jul 2007
 */

package suncertify.db;

/**
 * Exception thrown if there is a duplicate key.
 *
 * @author Richard Wardle
 */
public final class DuplicateKeyException extends Exception {

    /**
     * Creates a new <code>DuplicateKeyException</code> with
     * <code>message</code> and <code>cause</code> initialised to
     * <code>null</code>.
     */
    public DuplicateKeyException() {
        super();
    }

    /**
     * Creates a new instance of <code>DuplicateKeyException</code> with the
     * specified <code>message</code>, and with <code>cause</code>
     * initialised to <code>null</code>.
     *
     * @param message Error message.
     */
    public DuplicateKeyException(String message) {
        super(message);
    }
}
