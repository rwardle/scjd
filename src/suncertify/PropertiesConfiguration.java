/*
 * PropertiesConfiguration.java
 *
 * Created on 05-Jun-2007
 */

package suncertify;

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
 * Adapts <code>Properties</code> to the <code>Configuration</code>
 * interface.
 *
 * @author Richard Wardle
 */
public class PropertiesConfiguration implements Configuration {

    private static final Logger LOGGER 
            = Logger.getLogger(PropertiesConfiguration.class.getName());
    private final File propertiesFile;
    private final Properties properties;

    /**
     * Creates a new instance of <code>PropertiesConfiguration</code> using the
     * supplied properties file.
     * 
     * @param propertiesFile The properties file.
     * @throws IllegalArgumentException If the properties file is 
     * <code>null</code>.
     */
    public PropertiesConfiguration(File propertiesFile) {
        if (propertiesFile == null) {
            throw new IllegalArgumentException(
                    "propertiesFile must be non-null");
        }
        
        this.propertiesFile = propertiesFile;
        this.properties = new Properties();
    }

    /** {@inheritDoc} */
    public boolean exists() {
        return this.propertiesFile.exists();
    }

    /** {@inheritDoc} */
    public void load() throws ConfigurationException {
        InputStream in = null;
        try {
            in = new BufferedInputStream(
                    new FileInputStream(this.propertiesFile));
            this.properties.load(in);
        }
        catch (IOException e) {
            throw new ConfigurationException(e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error closing stream", e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void save() throws ConfigurationException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(
                    new FileOutputStream(this.propertiesFile));
            this.properties.store(out, null);
        }
        catch (IOException e) {
            throw new ConfigurationException(e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error closing stream", e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    public String getProperty(String name) {
        return this.properties.getProperty(name);
    }

    /** {@inheritDoc} */
    public void setProperty(String name, String value) {
        this.properties.setProperty(name, value);
    }
}
