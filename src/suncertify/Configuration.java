/*
 * Configuration.java
 *
 * Created on 05-Jun-2007
 */

package suncertify;

/**
 * Encapsulates application configuration information.
 *
 * @author Richard Wardle
 */
public interface Configuration {

    /**
     * Indicates if the configuration exists.
     * 
     * @return <code>true</code> if the configuration exists, 
     * <code>false</code> otherwise.
     */
    boolean exists();

    /**
     * Loads the configuration.
     * 
     * @throws ConfigurationException If the configuration cannot be loaded.
     */
    void load() throws ConfigurationException;

    /**
     * Saves the configuration.
     * 
     * @throws ConfigurationException If the configuration cannot be saved.
     */
    void save() throws ConfigurationException;

    /**
     * Gets the property with the supplied name.
     * 
     * @param name The property name.
     * @return The property.
     */
    String getProperty(String name);

    /**
     * Sets the property with the suppplied name to the supplied value.
     * 
     * @param name The property name.
     * @param value The property value.
     */
    void setProperty(String name, String value);
}
