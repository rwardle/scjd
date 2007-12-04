/*
 * Application.java
 *
 * 05 Jul 2007
 */

package suncertify;

/**
 * An application interface defining lifecycle methods. NOTE: All methods must
 * be called on the AWT event dispatching thread.
 * 
 * @author Richard Wardle
 */
public interface Application {

    /**
     * Initialises the application (obtaining and applying configuration, for
     * example) and returns a <code>boolean</code> flag indicating if the
     * initialisation completed successfully or not.
     * 
     * @return <code>true</code> if the initialisation completed successfully,
     *         <code>false</code> otherwise.
     */
    boolean initialise();

    /**
     * Starts the application. If this method completes successfully then the
     * application can be assumed to be up and running.
     * 
     * @throws FatalException
     *                 If the application cannot be started.
     */
    void startup() throws FatalException;

    /**
     * Handles the specified fatal exception. This could include informing the
     * user of the exception, logging the exception to a file, etc.
     * 
     * @param exception
     *                Fatal exception to handle.
     */
    void handleFatalException(FatalException exception);
}
