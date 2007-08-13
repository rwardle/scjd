/*
 * Application.java
 *
 * 05 Jul 2007
 */

package suncertify;

/**
 * Defines application lifecycle methods. NOTE: All methods must be called on
 * the AWT event dispatching thread.
 * 
 * @author Richard Wardle
 */
public interface Application {

    /**
     * Initialises the application, e.g. obtaining and applying configuration.
     * 
     * @throws ApplicationException
     *                 If the application cannot be initialised.
     */
    void initialise() throws ApplicationException;

    /**
     * Starts-up the application.
     * 
     * @throws ApplicationException
     *                 If the application cannot be started.
     */
    void startup() throws ApplicationException;

    /**
     * Handles the supplied application exception.
     * 
     * @param exception
     *                The exception to handle.
     */
    void handleException(ApplicationException exception);

    /**
     * Shuts-down the application.
     */
    void shutdown();
}
