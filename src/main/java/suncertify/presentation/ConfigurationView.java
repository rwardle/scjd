/*
 * ConfigurationView.java
 *
 * 06 Jun 2007
 */

package suncertify.presentation;

import java.awt.Component;


/**
 * An application configuration view.
 * 
 * @author Richard Wardle
 */
public interface ConfigurationView {

    /**
     * Sets the configuration presenter.
     * 
     * @param presenter
     *            Presenter to set.
     * @throws IllegalArgumentException
     *             If <code>presenter</code> is <code>null</code>.
     */
    void setPresenter(ConfigurationPresenter presenter);

    /**
     * Realises the view.
     */
    void realise();

    /**
     * Closes the view.
     */
    void close();

    /**
     * Returns the database file path.
     * 
     * @return The database file path.
     */
    String getDatabaseFilePath();

    /**
     * Sets the database file path.
     * 
     * @param databaseFilePath
     *            Database file path to set.
     */
    void setDatabaseFilePath(String databaseFilePath);

    /**
     * Returns the server address.
     * 
     * @return The server address.
     */
    String getServerAddress();

    /**
     * Sets the server address.
     * 
     * @param serverAddress
     *            Server address to set.
     */
    void setServerAddress(String serverAddress);

    /**
     * Gets the server port.
     * 
     * @return The server port.
     */
    Integer getServerPort();

    /**
     * Returns the server port.
     * 
     * @param serverPort
     *            Server port to set.
     * @throws IllegalArgumentException
     *             If <code>serverPort</code> is <code>null</code>.
     */
    void setServerPort(Integer serverPort);

    /**
     * Returns the view component.
     * 
     * @return The view component.
     */
    Component getComponent();
}
