/*
 * ConfigurationManager.java
 *
 * 05 Jun 2007
 */

package suncertify;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a simplified interface to the <code>Configuration</code> (facade
 * pattern).
 * 
 * @author Richard Wardle
 */
public final class ConfigurationManager {

    private static final Logger LOGGER = Logger
            .getLogger(ConfigurationManager.class.getName());
    private final Configuration configuration;

    /**
     * Creates a new instance of <code>ConfigurationManager</code> using the
     * supplied configuration.
     * 
     * @param configuration
     *                The configuration.
     * @throws IllegalArgumentException
     *                 If the configuration is <code>null</code>.
     */
    public ConfigurationManager(Configuration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must be non-null");
        }
        this.configuration = configuration;

        if (this.configuration.exists()) {
            try {
                this.configuration.load();
            } catch (ConfigurationException e) {
                ConfigurationManager.LOGGER.log(Level.WARNING,
                        "Error loading configuration", e);
            }
        }
        verifyConfiguration();
    }

    private void verifyConfiguration() {
        if (getDatabaseFilePath() == null) {
            setDatabaseFilePath(ApplicationConstants.DEFAULT_DATABASE_FILE_PATH);
        }
        if (getServerAddress() == null) {
            setServerAddress(ApplicationConstants.DEFAULT_SERVER_ADDRESS);
        }

        try {
            if (getServerPort() == null) {
                setServerPort(ApplicationConstants.DEFAULT_SERVER_PORT);
            }
        } catch (NumberFormatException e) {
            // Server port is not a number - reset to the default
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
        this.configuration.save();
    }

    /**
     * Gets the database file path.
     * 
     * @return The database file path.
     */
    public String getDatabaseFilePath() {
        return this.configuration
                .getProperty(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY);
    }

    /**
     * Sets the database file path.
     * 
     * @param databaseFilePath
     *                The database file path to set.
     * @throws IllegalArgumentException
     *                 If the supplied path is <code>null</code>.
     */
    public void setDatabaseFilePath(String databaseFilePath) {
        if (databaseFilePath == null) {
            throw new IllegalArgumentException(
                    "databaseFilePath must be non-null");
        }
        this.configuration.setProperty(
                ApplicationConstants.DATABASE_FILE_PATH_PROPERTY,
                databaseFilePath);
    }

    /**
     * Gets the server address.
     * 
     * @return The server address.
     */
    public String getServerAddress() {
        return this.configuration
                .getProperty(ApplicationConstants.SERVER_ADDRESS_PROPERTY);
    }

    /**
     * Sets the server address.
     * 
     * @param serverAddress
     *                The server address to set.
     * @throws IllegalArgumentException
     *                 If the supplied address is <code>null</code>.
     */
    public void setServerAddress(String serverAddress) {
        if (serverAddress == null) {
            throw new IllegalArgumentException("serverAddress must be non-null");
        }
        this.configuration.setProperty(
                ApplicationConstants.SERVER_ADDRESS_PROPERTY, serverAddress);
    }

    /**
     * Gets the server port.
     * 
     * @return The server port.
     * @throws NumberFormatException
     *                 If the server port property is not a number.
     */
    public Integer getServerPort() {
        return Integer.valueOf(this.configuration
                .getProperty(ApplicationConstants.SERVER_PORT_PROPERTY));
    }

    /**
     * Sets the server port.
     * 
     * @param serverPort
     *                The server port to set.
     * @throws IllegalArgumentException
     *                 If the supplied port is <code>null</code>.
     */
    public void setServerPort(Integer serverPort) {
        if (serverPort == null) {
            throw new IllegalArgumentException("serverPort must be non-null");
        }
        this.configuration.setProperty(
                ApplicationConstants.SERVER_PORT_PROPERTY, serverPort
                        .toString());
    }
}
