/*
 * ApplicationConstants.java
 *
 * 05 Jun 2007
 */

package suncertify;

import java.awt.Color;
import java.awt.Insets;
import java.util.ResourceBundle;

/**
 * Application constants.
 * 
 * @author Richard Wardle
 */
public class ApplicationConstants {

    /** Database file path property name. */
    public static final String DATABASE_FILE_PATH_PROPERTY = "suncertify.database-file-path";

    /** Server address property name. */
    public static final String SERVER_ADDRESS_PROPERTY = "suncertify.server-address";

    /** Server port property name. */
    public static final String SERVER_PORT_PROPERTY = "suncertify.server-port";

    /** Default database file path. */
    public static final String DEFAULT_DATABASE_FILE_PATH = System
            .getProperty("user.dir")
            + System.getProperty("file.separator") + "db-2x1.db";

    /** Default server address. */
    public static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1";

    /** Default server port. */
    public static final Integer DEFAULT_SERVER_PORT = Integer.valueOf("1099");

    /** Localhost IP address. */
    public static final String LOCALHOST_ADDRESS = "127.0.0.1";

    /** Name under which the remote service object is registered with RMI. */
    public static final String REMOTE_BROKER_SERVICE_NAME = "BrokerService";

    /** Default insets for the user interface. */
    public static final Insets DEFAULT_INSETS = new Insets(4, 4, 4, 4);

    /** Dark blue colour for gradient panels. */
    public static final Color DARK_BLUE = new Color(184, 206, 228);

    /** Light blue colour for gradient panels. */
    public static final Color LIGHT_BLUE = new Color(226, 236, 245);

    /** Dark grey colour for gradient panels. */
    public static final Color DARK_GREY = new Color(204, 205, 219);

    /** Light grey colour for gradient panels. */
    public static final Color LIGHT_GREY = new Color(221, 221, 221);

    /** Table column header names. */
    public static final String[] TABLE_COLUMN_NAMES = new String[6];
    static {
        // TODO Move these to the bundle in this package
        ResourceBundle resourceBundle = ResourceBundle
                .getBundle("suncertify/presentation/Bundle");
        TABLE_COLUMN_NAMES[0] = resourceBundle
                .getString("MainFrame.resultsTable.nameColumn.text");
        TABLE_COLUMN_NAMES[1] = resourceBundle
                .getString("MainFrame.resultsTable.locationColumn.text");
        TABLE_COLUMN_NAMES[2] = resourceBundle
                .getString("MainFrame.resultsTable.specialtiesColumn.text");
        TABLE_COLUMN_NAMES[3] = resourceBundle
                .getString("MainFrame.resultsTable.sizeColumn.text");
        TABLE_COLUMN_NAMES[4] = resourceBundle
                .getString("MainFrame.resultsTable.rateColumn.text");
        TABLE_COLUMN_NAMES[5] = resourceBundle
                .getString("MainFrame.resultsTable.ownerColumn.text");
    }

    public static final int TABLE_NAME_COLUMN_INDEX = 0;
    public static final int TABLE_LOCATION_COLUMN_INDEX = 1;
    public static final int TABLE_SPECIALTIES_COLUMN_INDEX = 2;
    public static final int TABLE_SIZE_COLUMN_INDEX = 3;
    public static final int TABLE_RATE_COLUMN_INDEX = 4;
    public static final int TABLE_OWNER_COLUMN_INDEX = 5;

    private ApplicationConstants() {
        // Disallow instantiation.
    }
}
