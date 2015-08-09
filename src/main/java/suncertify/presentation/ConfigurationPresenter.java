/*
 * ConfigurationPresenter.java
 *
 * 06 Jun 2007
 */

package suncertify.presentation;

import suncertify.ConfigurationManager;
import suncertify.ReturnStatus;

import javax.swing.*;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * A controller that is responsible for handling user events delegated to it from the
 * {@link ConfigurationView} and for updating the view based on the data in the
 * {@link ConfigurationManager}.
 *
 * @author Richard Wardle
 */
public class ConfigurationPresenter {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationPresenter.class.getName());

    private final ConfigurationManager configurationManager;
    private final ConfigurationView view;
    private ReturnStatus returnStatus;

    /**
     * Creates a new instance of <code>ConfigurationPresenter</code>.
     *
     * @param configurationManager Configuration manager.
     * @param view                 Configuration view.
     * @throws IllegalArgumentException If <code>configurationManager</code> or <code>view</code> is <code>null</code>.
     */
    public ConfigurationPresenter(ConfigurationManager configurationManager, ConfigurationView view) {
        if (configurationManager == null) {
            throw new IllegalArgumentException("configurationManager cannot be null");
        }
        if (view == null) {
            throw new IllegalArgumentException("view cannot be null");
        }

        this.configurationManager = configurationManager;
        this.view = view;
        returnStatus = ReturnStatus.CANCEL;
    }

    /**
     * Returns the return status of the configuration view.
     *
     * @return The return status.
     */
    public ReturnStatus getReturnStatus() {
        return returnStatus;
    }

    /**
     * Realises the view.
     */
    public void realiseView() {
        loadViewFromModel();
        view.realise();
    }

    private void loadViewFromModel() {
        String databaseFilePath = configurationManager.getDatabaseFilePath();
        String serverAddress = configurationManager.getServerAddress();
        Integer serverPort = configurationManager.getServerPort();

        LOGGER.info("Initial configuration: databaseFilePath=" + databaseFilePath
                + ", serverAddress=" + serverAddress + ", serverPort=" + serverPort);

        view.setDatabaseFilePath(databaseFilePath);
        view.setServerAddress(serverAddress);
        view.setServerPort(serverPort);
    }

    /**
     * Performs the OK button action.
     */
    public final void okButtonActionPerformed() {
        returnStatus = ReturnStatus.OK;
        saveViewToModel();
        view.close();
    }

    private void saveViewToModel() {
        String databaseFilePath = view.getDatabaseFilePath();
        String serverAddress = view.getServerAddress();
        Integer serverPort = view.getServerPort();

        LOGGER.info("Final configuration: databaseFilePath=" + databaseFilePath
                + ", serverAddress=" + serverAddress + ", serverPort=" + serverPort);

        configurationManager.setDatabaseFilePath(databaseFilePath);
        configurationManager.setServerAddress(serverAddress);
        configurationManager.setServerPort(serverPort);
    }

    /**
     * Performs the Cancel button action.
     */
    public final void cancelButtonActionPerformed() {
        view.close();
    }

    /**
     * Performs the Browse button action.
     */
    public final void browseButtonActionPerformed() {
        JFileChooser fileChooser = createFileChooser(view.getDatabaseFilePath());
        int option = fileChooser.showDialog(
                view.getComponent(),
                ResourceBundle.getBundle("suncertify/presentation/Bundle").getString(
                        "ConfigurationPresenter.approveButton.text"));
        if (option == JFileChooser.APPROVE_OPTION) {
            view.setDatabaseFilePath(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    JFileChooser createFileChooser(String directoryPath) {
        return new JFileChooser(directoryPath);
    }
}
