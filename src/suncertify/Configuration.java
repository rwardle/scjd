/*
 * Configuration.java
 *
 * Created on 05-Jun-2005
 */


package suncertify;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


/**
 * Encapsulates application configuration information. Reads and writes to
 * permanent storage in the form of a properties file.
 *
 * @author Richard Wardle
 */
public class Configuration {

    private Properties properties;

    /**
     * Creates a new instance of <code>Configuration</code>.
     *
     * @param properties The properties.
     */
    public Configuration(Properties properties) {
        if (properties == null) {
            throw new NullPointerException(
                    "properties argument must be non-null");
        }

        this.properties = properties;
    }

    /**
     * Loads the configuration using the supplied input stream.
     *
     * @param in The input stream.
     * @throws IOException If there is an error reading from the input stream.
     * @throws NullPointerException If the input stream is <code>null</code>.
     */
    public void loadConfiguration(InputStream in) throws IOException {
        if (in == null) {
            throw new NullPointerException("Input stream must be non-null");
        }

        this.properties.load(in);
    }

    /**
     * Saves the configuration using the supplied output stream.
     *
     * @param out The output stream.
     * @throws IOException If there is an error writing to the output stream.
     * @throws NullPointerException If the output stream is <code>null</code>.
     */
    public void saveConfiguration(OutputStream out) throws IOException {
        if (out == null) {
            throw new NullPointerException("Output stream must be non-null");
        }

        this.properties.store(out, "This file stores configuration properties "
                + "between application runs.");
    }

    /**
     * Gets the databaseFilePath.
     *
     * @return The databaseFilePath.
     */
    public String getDatabaseFilePath() {
        return this.properties.getProperty(
                ApplicationConstants.DATABASE_FILE_PATH_PROPERTY);
    }

    /**
     * Sets the databaseFilePath.
     *
     * @param databaseFilePath The databaseFilePath to set.
     * @throws NullPointerException If the <code>databaseFilePath</code>
     * parameter is <code>null</code>.
     */
    public void setDatabaseFilePath(String databaseFilePath) {
        if (databaseFilePath == null) {
            throw new NullPointerException(
                    "The parameter databaseFilePath must be non-null");
        }

        this.properties.setProperty(
                ApplicationConstants.DATABASE_FILE_PATH_PROPERTY,
                databaseFilePath);
    }

    /**
     * Gets the serverAddress.
     *
     * @return The serverAddress.
     */
    public String getServerAddress() {
        return this.properties.getProperty(
                ApplicationConstants.SERVER_ADDRESS_PROPERTY);
    }

    /**
     * Sets the serverAddress.
     *
     * @param serverAddress The serverAddress to set.
     * @throws NullPointerException If the <code>serverAddress</code> parameter
     * is <code>null</code>.
     */
    public void setServerAddress(String serverAddress) {
        if (serverAddress == null) {
            throw new NullPointerException(
                    "The parameter serverAddress must be non-null");
        }

        this.properties.setProperty(
                ApplicationConstants.SERVER_ADDRESS_PROPERTY, serverAddress);
    }

    /**
     * Gets the serverPort.
     *
     * @return The serverPort.
     */
    public String getServerPort() {
        return this.properties.getProperty(
                ApplicationConstants.SERVER_PORT_PROPERTY);
    }

    /**
     * Sets the serverPort.
     *
     * @param serverPort The serverPort to set.
     * @throws NullPointerException If the <code>serverPort</code> parameter is
     * <code>null</code>.
     */
    public void setServerPort(String serverPort) {
        if (serverPort == null) {
            throw new NullPointerException(
                    "The parameter serverPort must be non-null");
        }

        this.properties.setProperty(ApplicationConstants.SERVER_PORT_PROPERTY,
                serverPort);
    }
}
