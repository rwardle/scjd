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
     * @return <code>true</code> if the intialisation completed successfully,
     *         <code>false</code> otherwise.
     */
    boolean initialise();

    /**
     * Starts-up the application.
     * 
     * @throws FatalException
     *                 If the application cannot be started.
     */
    void startup() throws FatalException;

    /**
     * Handles the supplied fatal exception.
     * 
     * @param exception
     *                The exception to handle.
     */
    void handleFatalException(FatalException exception);
}
