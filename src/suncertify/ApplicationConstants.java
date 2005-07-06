/*
 * ApplicationConstants.java
 *
 * Created on 05-Jun-2005
 */


package suncertify;

/**
 * Application constants.
 *
 * @author Richard Wardle
 */
public final class ApplicationConstants {

    /** Server address property name. */
    public static final String ADDRESS_PROPERTY = "suncertify.server-address";

    /** Database file path property name. */
    public static final String PATH_PROPERTY = "suncertify.database-file-path";

    /** Server port property name. */
    public static final String PORT_PROPERTY = "suncertify.server-port";

    // TODO: use localhost?
    /** Default value used for the server's address. */
    public static final String DEFAULT_ADDRESS = "127.0.0.1";

    /** Default value used for the database file path. */
    public static final String DEFAULT_PATH = System.getProperty("user.dir")
            + System.getProperty("file.separator") + "db-2x1.db";

    /** Default value used for the server's port number. */
    public static final String DEFAULT_PORT = "1099";

    /** Name under which the remote service object is registered with RMI. */
    public static final String REMOTE_BROKER_SERVICE_NAME = "BrokerService";

    private ApplicationConstants() {
        // Disallow instantiation.
    }
}
