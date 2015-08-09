/*
 * PropertiesConfiguration.java
 *
 * 05 Jun 2007
 */

package suncertify;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of {@link Configuration} that uses a properties file to store application
 * configuration.
 *
 * @author Richard Wardle
 */
public final class PropertiesConfiguration implements Configuration {

    private static final Logger LOGGER = Logger.getLogger(PropertiesConfiguration.class.getName());

    private final File propertiesFile;
    private final Properties properties;

    /**
     * Creates a new instance of <code>PropertiesConfiguration</code> using the specified properties
     * file.
     *
     * @param propertiesFile Properties file.
     * @throws IllegalArgumentException If the <code>propertiesFile</code> is <code>null</code>.
     */
    public PropertiesConfiguration(File propertiesFile) {
        if (propertiesFile == null) {
            throw new IllegalArgumentException("propertiesFile cannot be null");
        }

        this.propertiesFile = propertiesFile;
        properties = new Properties();
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists() {
        return propertiesFile.exists();
    }

    /**
     * {@inheritDoc}
     */
    public void load() throws ConfigurationException {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(propertiesFile));
            properties.load(in);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error closing stream", e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void save() throws ConfigurationException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(propertiesFile));
            properties.store(out, null);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error closing stream", e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        return properties.getProperty(name);
    }

    /**
     * {@inheritDoc}
     */
    public void setProperty(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        properties.setProperty(name, value);
    }
}
