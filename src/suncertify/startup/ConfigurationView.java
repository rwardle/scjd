/*
 * ConfigurationView.java
 *
 * Created on 06-Jun-2005
 */


package suncertify.startup;

import java.awt.event.ActionListener;


/**
 * The application configuration view.
 *
 * @author Richard Wardle
 */
public interface ConfigurationView {

    /**
     * Initialise the view's components. This method should not realise the
     * components.
     */
    void initialiseComponents();


    /**
     * Realises the view and its components.
     */
    void realiseView();

    /**
     * Closes the view.
     */
    void close();

    /**
     * Adds an action listener fall the OK button.
     *
     * @param listener The action listener.
     */
    void addCancelButtonListener(ActionListener listener);

    /**
     * Adds an action listener for the Cancel button.
     *
     * @param listener The action listener.
     */
    void addOkButtonListener(ActionListener listener);

    /**
     * Gets the database file path.
     *
     * @return The database file path.
     */
    String getDatabaseFilePath();

    /**
     * Sets the database file path.
     *
     * @param databaseFilePath The database file path to set.
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
     * @param serverAddress The server address to set.
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
     * @param serverPort The server port to set.
     */
    void setServerPort(String serverPort);
}
