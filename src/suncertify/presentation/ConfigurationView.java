/*
 * ConfigurationView.java
 *
 * 06 Jun 2007
 */

package suncertify.presentation;

/**
 * The application configuration view.
 * 
 * @author Richard Wardle
 */
public interface ConfigurationView {

    /**
     * Sets the <code>ConfigurationPresenter</code>.
     * 
     * @param presenter
     *                The presenter.
     */
    void setPresenter(ConfigurationPresenter presenter);

    /**
     * Realises the view and its components.
     */
    void realise();

    /**
     * Closes the view.
     */
    void close();

    /**
     * Gets the database file path.
     * 
     * @return The database file path.
     */
    String getDatabaseFilePath();

    /**
     * Sets the database file path.
     * 
     * @param databaseFilePath
     *                The database file path to set.
     */
    void setDatabaseFilePath(String databaseFilePath);

    /**
     * Gets the server address.
     * 
     * @return The server address.
     */
    String getServerAddress();

    /**
     * Sets the server address.
     * 
     * @param serverAddress
     *                The server address to set.
     */
    void setServerAddress(String serverAddress);

    /**
     * Gets the server port.
     * 
     * @return The server port.
     */
    String getServerPort();

    /**
     * Sets the server port.
     * 
     * @param serverPort
     *                The server port to set.
     */
    void setServerPort(String serverPort);
}
