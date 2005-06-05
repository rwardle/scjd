/*
 * DatabaseConfigurationConstants.java
 *
 * Created on 05-Jun-2005
 */

package suncertify.startup;

/**
 * Constants for use in database configuration.
 *
 * @author Richard Wardle
 */
public final class DatabaseConfigurationConstants {

    /** Server IP address property name. */
    public static final String ADDRESS_PROPERTY = "suncertify.startup"
            + ".server-ip-address";

    // TODO: use localhost?
    /** Default value used for the server's IP address. */
    public static final String DEFAULT_ADDRESS = "127.0.0.1";

    /** Default value used for the database file path. */
    public static final String DEFAULT_PATH = System.getProperty("user.dir")
            + System.getProperty("file.separator") + "db-2x1.db";

    /** Default value used for the server's port number. */
    public static final String DEFAULT_PORT = "1099";

    /** Database file path property name. */
    public static final String PATH_PROPERTY = "suncertify.startup"
            + ".database-file-path";

    /** Server port property name. */
    public static final String PORT_PROPERTY = "suncertify.startup.server-port";

    private DatabaseConfigurationConstants() {
    // No external instantiation
    }
}
