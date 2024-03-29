/*
 * ApplicationConstants.java
 *
 * 05 Jun 2007
 */

package suncertify;

/**
 * Constants for the application layer.
 *
 * @author Richard Wardle
 */
public class ApplicationConstants {

    /**
     * Database file path property name.
     */
    public static final String DATABASE_FILE_PATH_PROPERTY = "suncertify.database-file-path";

    /**
     * Server address property name.
     */
    public static final String SERVER_ADDRESS_PROPERTY = "suncertify.server-address";

    /**
     * Server port property name.
     */
    public static final String SERVER_PORT_PROPERTY = "suncertify.server-port";

    /**
     * Default database file path.
     */
    public static final String DEFAULT_DATABASE_FILE_PATH = System.getProperty("user.dir")
            + System.getProperty("file.separator") + "db-2x1.db";

    /**
     * Default server address.
     */
    public static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1";

    /**
     * Default server port.
     */
    public static final Integer DEFAULT_SERVER_PORT = Integer.valueOf("1099");

    /**
     * Localhost IP address.
     */
    public static final String LOCALHOST_ADDRESS = "127.0.0.1";

    /**
     * Name under which the remote broker service object is bound in the RMI registry.
     */
    public static final String REMOTE_BROKER_SERVICE_NAME = "BrokerService";

    private ApplicationConstants() {
        // Disallow instantiation.
    }
}
