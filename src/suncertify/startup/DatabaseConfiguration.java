/*
 * DatabaseConfiguration.java
 *
 * Created on 05-Jun-2005
 */

package suncertify.startup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Encapsulates database configuration, reading and writing to permanent storage
 * in the form of a properties file.
 *
 * @author Richard Wardle
 */
public class DatabaseConfiguration {

    private static Logger logger = Logger.getLogger(DatabaseConfiguration.class
            .getName());

    private String databaseFilePath;
    private String propertiesFilePath;
    private String serverIpAddress;
    private String serverPort;

    /**
     * Creates a new DatabaseConfiguration with the default configuration.
     *
     * @param propertiesFilePath
     *        The path to the properties file that holds the configuration.
     * @throws IllegalArgumentException
     *         If the propertiesFilePath is null or is an empty string.
     */
    public DatabaseConfiguration(String propertiesFilePath) {
        if (propertiesFilePath == null || propertiesFilePath.equals("")) {
            throw new IllegalArgumentException(
                    "propertiesFilePath should be non-null and should not be "
                            + "an empty string.");
        }

        this.propertiesFilePath = propertiesFilePath;
        this.serverIpAddress = DatabaseConfigurationConstants.DEFAULT_ADDRESS;
        this.serverPort = DatabaseConfigurationConstants.DEFAULT_PORT;
        this.databaseFilePath = DatabaseConfigurationConstants.DEFAULT_PATH;
    }

    /**
     * Gets the databaseFilePath.
     *
     * @return Returns the databaseFilePath.
     */
    public String getDatabaseFilePath() {
        return this.databaseFilePath;
    }

    /**
     * Gets the serverIpAddress.
     *
     * @return Returns the serverIpAddress.
     */
    public String getServerIpAddress() {
        return this.serverIpAddress;
    }

    /**
     * Gets the serverPort.
     *
     * @return Returns the serverPort.
     */
    public String getServerPort() {
        return this.serverPort;
    }

    /**
     * Loads the configuration from the properties file (if it exists).
     *
     * @return true if the configuration was loaded from the properties file.
     */
    public boolean loadConfiguration() {
        boolean loaded = false;
        File propertiesFile = new File(this.propertiesFilePath);

        if (propertiesFile.exists()) {
            InputStream in = null;
            try {
                in = new BufferedInputStream(
                        new FileInputStream(propertiesFile));
                Properties properties = new Properties();
                properties.load(in);
                getProperties(properties);
                loaded = true;
            } catch (IOException e) {
                logger.log(Level.WARNING,
                        "Error reading from properties file at: '"
                                + propertiesFile.getPath() + "'.", e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.warning("Error closing InputStream for file: '"
                                + propertiesFile.getPath() + "'.");
                    }
                }
            }
        }

        return loaded;
    }

    /**
     * Saves the configuration to the properties file, creating it if necessary.
     *
     * @throws IOException
     *         If the properties cannot be written to the file.
     */
    public void saveConfiguration() throws IOException {
        Properties properties = new Properties();
        setProperties(properties);

        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(
                    this.propertiesFilePath));
            properties.store(out,
                    "This file stores database configuration properties "
                            + "between application runs.");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.warning("Error closing OutputStream for file: '"
                            + this.propertiesFilePath + "'.");
                }
            }
        }
    }

    /**
     * Sets the databaseFilePath.
     *
     * @param databaseFilePath
     *        The databaseFilePath to set.
     */
    public void setDatabaseFilePath(String databaseFilePath) {
        this.databaseFilePath = databaseFilePath;
    }

    /**
     * Sets the serverIpAddress.
     *
     * @param serverIpAddress
     *        The serverIpAddress to set.
     */
    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    /**
     * Sets the serverPort.
     *
     * @param serverPort
     *        The serverPort to set.
     */
    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    private void getProperties(Properties properties) {
        String ip = properties
                .getProperty(DatabaseConfigurationConstants.ADDRESS_PROPERTY);
        if (ip != null && !ip.equals("")) {
            setServerIpAddress(ip);
        }

        String port = properties
                .getProperty(DatabaseConfigurationConstants.PORT_PROPERTY);
        if (port != null && !port.equals("")) {
            setServerPort(port);
        }

        String path = properties
                .getProperty(DatabaseConfigurationConstants.PATH_PROPERTY);
        if (path != null && !path.equals("")) {
            setDatabaseFilePath(path);
        }
    }

    private void setProperties(Properties properties) {
        properties.setProperty(DatabaseConfigurationConstants.ADDRESS_PROPERTY,
                getServerIpAddress());
        properties.setProperty(DatabaseConfigurationConstants.PORT_PROPERTY,
                getServerPort());
        properties.setProperty(DatabaseConfigurationConstants.PATH_PROPERTY,
                getDatabaseFilePath());
    }
}
