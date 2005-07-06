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
     * Configures the application. Loads any existing configuration from
     * persistent storage, presents it to the user for modification, and saves
     * it to persistent storage.
     *
     * @param configuration A <code>Configuration</code> object in which the
     * result of the configuration process will be stored.
     * @return <code>true</code> if the user completed the configuration
     * process, <code>false</code> otherwise.
     */
    boolean configure(Configuration configuration);

    /**
     * Runs the application using the configuration information supplied.
     *
     * @param configuration The application configuration.
     */
    void run(Configuration configuration);
}
