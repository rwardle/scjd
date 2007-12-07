/*
 * ConfigurationManager.java
 *
 * 05 Jun 2007
 */

package suncertify;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a simplified interface for {@link Configuration} (facade pattern).
 * 
 * @author Richard Wardle
 */
public final class ConfigurationManager {

    private static final Logger LOGGER = Logger
            .getLogger(ConfigurationManager.class.getName());

    private final Configuration configuration;

    /**
     * Creates a new instance of <code>ConfigurationManager</code> using the
     * specified configuration.
     * 
     * @param configuration
     *                Configuration.
     * @throws IllegalArgumentException
     *                 If the <code>configuration</code> is <code>null</code>.
     */
    public ConfigurationManager(Configuration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration cannot be null");
        }
        this.configuration = configuration;

        // Load any existing configuration
        if (this.configuration.exists()) {
            try {
                LOGGER.info("Loading existing configuration");
                this.configuration.load();
            } catch (ConfigurationException e) {
                LOGGER.log(Level.WARNING,
                        "Error loading configuration, using default values", e);
            }
        }

        verifyConfiguration();
    }

    private void verifyConfiguration() {
        /*
         * Verify that the configuration is complete and valid, use default
         * values if not.
         */

        if (getDatabaseFilePath() == null) {
            LOGGER.info("Using default database file path");
            setDatabaseFilePath(ApplicationConstants.DEFAULT_DATABASE_FILE_PATH);
        }
        if (getServerAddress() == null) {
            LOGGER.info("Using default server address");
            setServerAddress(ApplicationConstants.DEFAULT_SERVER_ADDRESS);
        }

        try {
            getServerPort();
        } catch (NumberFormatException e) {
            LOGGER.info("Using default server port");
            setServerPort(ApplicationConstants.DEFAULT_SERVER_PORT);
        }
    }

    /**
     * Saves the configuration.
     * 
     * @throws ConfigurationException
     *                 If the configuration cannot be saved.
     */
    public void save() throws ConfigurationException {
        configuration.save();
    }

    /**
     * Gets the database file path.
     * 
     * @return The database file path.
     */
    public String getDatabaseFilePath() {
        return configuration
                .getProperty(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY);
    }

    /**
     * Sets the database file path.
     * 
     * @param databaseFilePath
     *                Database file path to set.
     * @throws IllegalArgumentException
     *                 If <code>databaseFilePath</code> is <code>null</code>.
     */
    public void setDatabaseFilePath(String databaseFilePath) {
        if (databaseFilePath == null) {
            throw new IllegalArgumentException(
                    "databaseFilePath cannot be null");
        }
        configuration.setProperty(
                ApplicationConstants.DATABASE_FILE_PATH_PROPERTY,
                databaseFilePath);
    }

    /**
     * Gets the server address.
     * 
     * @return The server address.
     */
    public String getServerAddress() {
        return configuration
                .getProperty(ApplicationConstants.SERVER_ADDRESS_PROPERTY);
    }

    /**
     * Sets the server address.
     * 
     * @param serverAddress
     *                Server address to set.
     * @throws IllegalArgumentException
     *                 If <code>serverAddress</code> is <code>null</code>.
     */
    public void setServerAddress(String serverAddress) {
        if (serverAddress == null) {
            throw new IllegalArgumentException("serverAddress cannot be null");
        }
        configuration.setProperty(ApplicationConstants.SERVER_ADDRESS_PROPERTY,
                serverAddress);
    }

    /**
     * Gets the server port.
     * 
     * @return The server port.
     * @throws NumberFormatException
     *                 If the server port property is <code>null</code> or is
     *                 not a number.
     */
    public Integer getServerPort() {
        return Integer.valueOf(configuration
                .getProperty(ApplicationConstants.SERVER_PORT_PROPERTY));
    }

    /**
     * Sets the server port.
     * 
     * @param serverPort
     *                Server port to set.
     * @throws IllegalArgumentException
     *                 If <code>serverPort</code> is <code>null</code>.
     */
    public void setServerPort(Integer serverPort) {
        if (serverPort == null) {
            throw new IllegalArgumentException("serverPort cannot be null");
        }
        configuration.setProperty(ApplicationConstants.SERVER_PORT_PROPERTY,
                serverPort.toString());
    }
}
