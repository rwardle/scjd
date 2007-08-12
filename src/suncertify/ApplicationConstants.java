/*
 * ApplicationConstants.java
 *
 * Created on 05-Jun-2007
 */

package suncertify;

/**
 * Application constants.
 * 
 * @author Richard Wardle
 */
public class ApplicationConstants {

    /** Database file path property name. */
    public static final String DATABASE_FILE_PATH_PROPERTY 
            = "suncertify.database-file-path";
    
    /** Server address property name. */
    public static final String SERVER_ADDRESS_PROPERTY 
            = "suncertify.server-address";
    
    /** Server port property name. */
    public static final String SERVER_PORT_PROPERTY 
            = "suncertify.server-port";
    
    /** Default database file path. */
    public static final String DEFAULT_DATABASE_FILE_PATH 
            = System.getProperty("user.dir") 
            + System.getProperty("file.separator") + "db-2x1.db";

    /** Default server address. */
    public static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1";

    /** Default server port. */
    public static final String DEFAULT_SERVER_PORT = "1099";

    /** Name under which the remote service object is registered with RMI. */
    public static final String REMOTE_BROKER_SERVICE_NAME = "BrokerService";

    private ApplicationConstants() {
        // Disallow instantiation.
    }
}
