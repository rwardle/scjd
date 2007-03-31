/*
 * ApplicationConstants.java
 *
 * Created on 05-Jun-2005
 */

package suncertify;

import java.util.Properties;

/**
 * Application constants.
 *
 * @author Richard Wardle
 */
public class ApplicationConstants {

    /** Database file path property name. */
    public static final String DATABASE_FILE_PATH_PROPERTY =
            "suncertify.database-file-path";

    /** Server address property name. */
    public static final String SERVER_ADDRESS_PROPERTY =
            "suncertify.server-address";

    /** Server port property name. */
    public static final String SERVER_PORT_PROPERTY = "suncertify.server-port";

    /** Default configuration properties. */
    public static final Properties DEFAULT_PROPERTIES = new Properties();

    static {
        ApplicationConstants.DEFAULT_PROPERTIES.setProperty(
                ApplicationConstants.DATABASE_FILE_PATH_PROPERTY,
                System.getProperty("user.dir")
                        + System.getProperty("file.separator") + "db-2x1.db");
        ApplicationConstants.DEFAULT_PROPERTIES.setProperty(
                ApplicationConstants.SERVER_ADDRESS_PROPERTY, "127.0.0.1");
        ApplicationConstants.DEFAULT_PROPERTIES.setProperty(
                ApplicationConstants.SERVER_PORT_PROPERTY, "1099");
    }

    /** Name under which the remote service object is registered with RMI. */
    public static final String REMOTE_BROKER_SERVICE_NAME = "BrokerService";

    private ApplicationConstants() {
        // Disallow instantiation.
    }
}
