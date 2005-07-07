/*
 * Application.java
 *
 * Created on 06-Jul-2005
 */


package suncertify;


/**
 * The application.
 *
 * @author Richard Wardle
 */
public interface Application {

    /**
     * Configures the application.
     *
     * @return <code>true</code> if the user completed the configuration
     * process, <code>false</code> otherwise.
     */
    boolean configure();

    /**
     * Runs the application.
     */
    void run();
}
