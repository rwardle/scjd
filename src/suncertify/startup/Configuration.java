/*
 * Configuration.java
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
 * Encapsulates application configuration properties, reading and writing to
 * permanent storage in the form of a properties file.
 * 
 * @author Richard Wardle
 */
public class Configuration {

    private static Logger logger = Logger.getLogger(Configuration.class
            .getName());

    private String databaseFilePath;
    private String propertiesFilePath;
    private String serverAddress;
    private String serverPort;

    /**
     * Creates a new Configuration with the default configuration.
     * 
     * @param propertiesFilePath
     *        The path to the properties file that holds the configuration.
     * @throws IllegalArgumentException
     *         If the propertiesFilePath is null or is an empty string.
     */
    public Configuration(String propertiesFilePath) {
        if (propertiesFilePath == null || propertiesFilePath.equals("")) {
            throw new IllegalArgumentException(
                    "propertiesFilePath should be non-null and should not be "
                            + "an empty string.");
        }

        this.propertiesFilePath = propertiesFilePath;
        this.serverAddress = ConfigurationConstants.DEFAULT_ADDRESS;
        this.serverPort = ConfigurationConstants.DEFAULT_PORT;
        this.databaseFilePath = ConfigurationConstants.DEFAULT_PATH;
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
     * Gets the propertiesFilePath.
     * 
     * @return Returns the propertiesFilePath.
     */
    public String getPropertiesFilePath() {
        return this.propertiesFilePath;
    }

    /**
     * Gets the serverAddress.
     * 
     * @return Returns the serverAddress.
     */
    public String getServerAddress() {
        return this.serverAddress;
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
                // If there is an error reading from the properties file we want
                // to fall back gracefully to using the default configuration,
                // so we catch this exception, log it and continue.
                logger.log(Level.WARNING,
                        "Error reading from properties file at: '"
                                + propertiesFile.getPath()
                                + "'. Falling back to default configuration.",
                        e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.log(Level.WARNING,
                                "Error closing InputStream for file: '"
                                        + propertiesFile.getPath() + "'.", e);
                    }
                }
            }
        } else {
            logger.info("Properties file doesn't exist, using default "
                    + "configuration (path='" + propertiesFile.getPath()
                    + "').");
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
                    logger.log(Level.WARNING,
                            "Error closing OutputStream for file: '"
                                    + this.propertiesFilePath + "'.", e);
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
     * Sets the serverAddress.
     * 
     * @param serverAddress
     *        The serverAddress to set.
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
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
        String address = properties
                .getProperty(ConfigurationConstants.ADDRESS_PROPERTY);
        if (address != null && !address.equals("")) {
            setServerAddress(address);
        }

        String port = properties
                .getProperty(ConfigurationConstants.PORT_PROPERTY);
        if (port != null && !port.equals("")) {
            setServerPort(port);
        }

        String path = properties
                .getProperty(ConfigurationConstants.PATH_PROPERTY);
        if (path != null && !path.equals("")) {
            setDatabaseFilePath(path);
        }
    }

    private void setProperties(Properties properties) {
        properties.setProperty(ConfigurationConstants.ADDRESS_PROPERTY,
                getServerAddress());
        properties.setProperty(ConfigurationConstants.PORT_PROPERTY,
                getServerPort());
        properties.setProperty(ConfigurationConstants.PATH_PROPERTY,
                getDatabaseFilePath());
    }
}
