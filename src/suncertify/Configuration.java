/*
 * Configuration.java
 *
 * 05 Jun 2007
 */

package suncertify;

/**
 * Application configuration information.
 * 
 * @author Richard Wardle
 */
public interface Configuration {

    /**
     * Indicates if the configuration exists.
     * 
     * @return <code>true</code> if the configuration exists, <code>false</code> otherwise.
     */
    boolean exists();

    /**
     * Loads the configuration.
     * 
     * @throws ConfigurationException
     *             If the configuration cannot be loaded.
     */
    void load() throws ConfigurationException;

    /**
     * Saves the configuration.
     * 
     * @throws ConfigurationException
     *             If the configuration cannot be saved.
     */
    void save() throws ConfigurationException;

    /**
     * Returns the property with the specified name.
     * 
     * @param name
     *            Property name.
     * @return The property.
     * @throws IllegalArgumentException
     *             If <code>name</code> is <code>null</code>.
     */
    String getProperty(String name);

    /**
     * Sets the property with the specified name to the specified value.
     * 
     * @param name
     *            Property name.
     * @param value
     *            Property value.
     * @throws IllegalArgumentException
     *             If <code>name</code> or <code>value</code> are <code>null</code>.
     */
    void setProperty(String name, String value);
}
