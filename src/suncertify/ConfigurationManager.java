/*
 * ConfigurationManager.java
 *
 * 05 Jun 2007
 */

package suncertify;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a simplified interface for <code>Configuration</code> (façade
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
     * specified configuration.
     * 
     * @param configuration
     *                Configuration.
     * @throws IllegalArgumentException
     *                 If the <code>configuration</code> is <code>null</code>.
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
                        "Error loading configuration, using default values", e);
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
            getServerPort();
        } catch (NumberFormatException e) {
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
     *                Database file path to set.
     * @throws IllegalArgumentException
     *                 If <code>databaseFilePath</code> is <code>null</code>.
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
     *                Server address to set.
     * @throws IllegalArgumentException
     *                 If <code>serverAddress</code> is <code>null</code>.
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
     *                Server port to set.
     * @throws IllegalArgumentException
     *                 If <code>serverPort</code> is <code>null</code>.
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
