/*
 * ExceptionHandler.java
 *
 * 05 Jul 2007
 */

package suncertify;

/**
 * Defines an application exception handler.
 * 
 * @author Richard Wardle
 */
public interface ExceptionHandler {

    /**
     * Handles the application exception.
     * 
     * @param exception
     *                The application exception.
     */
    void handleException(ApplicationException exception);
}
