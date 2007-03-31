/*
 * Application.java
 *
 * Created on 06-Jul-2005
 */

package suncertify;

import java.io.File;

/**
 * The application.
 *
 * @author Richard Wardle
 */
public interface Application {

    /**
     * Configures the application. Loads any existing configuration from the
     * supplied properties file, presents it to the user for modification, and
     * saves it to the properties file.
     *
     * @param propertiesFile The properties file.
     * @return <code>true</code> if the user completed the configuration
     * process, <code>false</code> otherwise.
     * @throws ApplicationException If there is an error in the configuration
     * process.
     * @throws NullPointerException If the <code>propertiesFile</code>
     * parameter is <code>null</code>.
     */
    boolean configure(File propertiesFile) throws ApplicationException;

    // TODO: Change this method name to avoid confusion with Runnable?
    /**
     * Runs the application.
     *
     * @throws ApplicationException If there is an error running the
     * application.
     */
    void run() throws ApplicationException;

    /**
     * Displays an error dialog showing the supplied message.
     *
     * @param message The message to show.
     * @throws ApplicationException If there is an error displaying the error
     * dialog.
     */
    void showErrorDialog(String message) throws ApplicationException;

    /**
     * Exits the application.
     *
     * @param status The exit status.
     */
    void exit(int status);
}
