/*
 * ConfigurationPresenter.java
 *
 * 06 Jun 2007
 */

package suncertify.presentation;

import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import suncertify.ConfigurationManager;
import suncertify.ReturnStatus;

/**
 * Encapsulates the presentation behaviour of the configuration dialog.
 * 
 * @author Richard Wardle
 */
public class ConfigurationPresenter {

    private final ConfigurationManager configurationManager;
    private final ConfigurationView view;
    private ReturnStatus returnStatus;

    /**
     * Creates a new instance of <code>ConfigurationPresenter</code>.
     * 
     * @param configurationManager
     *                The configuration model.
     * @param view
     *                The configuration view.
     * @throws IllegalArgumentException
     *                 If the configuration or view is <code>null</code>.
     */
    public ConfigurationPresenter(ConfigurationManager configurationManager,
            ConfigurationView view) {
        if (configurationManager == null || view == null) {
            throw new IllegalArgumentException(
                    "configurationManager and view must be non-null");
        }

        this.configurationManager = configurationManager;
        this.view = view;
        this.returnStatus = ReturnStatus.CANCEL;
    }

    /**
     * Gets the returnStatus.
     * 
     * @return The returnStatus.
     */
    public ReturnStatus getReturnStatus() {
        return this.returnStatus;
    }

    /** Realises the view. */
    public void realiseView() {
        loadViewFromModel();
        this.view.realise();
    }

    private void loadViewFromModel() {
        this.view.setDatabaseFilePath(this.configurationManager
                .getDatabaseFilePath());
        this.view
                .setServerAddress(this.configurationManager.getServerAddress());
        this.view.setServerPort(this.configurationManager.getServerPort());
    }

    /** Performs the OK button action. */
    public void okButtonActionPerformed() {
        // TODO Validation of URL/file here?
        this.returnStatus = ReturnStatus.OK;
        saveViewToModel();
        this.view.close();
    }

    private void saveViewToModel() {
        this.configurationManager.setDatabaseFilePath(this.view
                .getDatabaseFilePath());
        this.configurationManager
                .setServerAddress(this.view.getServerAddress());
        this.configurationManager.setServerPort(this.view.getServerPort());
    }

    /** Performs the Cancel button action. */
    public void cancelButtonActionPerformed() {
        this.view.close();
    }

    /** Performs the Browse button action. */
    public void browseButtonActionPerformed() {
        JFileChooser fileChooser = createFileChooser(this.view
                .getDatabaseFilePath());
        int option = fileChooser
                .showDialog(this.view.getComponent(), ResourceBundle.getBundle(
                        "suncertify/presentation/Bundle").getString(
                        "ConfigurationPresenter.approveButton.text"));
        if (option == JFileChooser.APPROVE_OPTION) {
            this.view.setDatabaseFilePath(fileChooser.getSelectedFile()
                    .getAbsolutePath());
        }
    }

    JFileChooser createFileChooser(String directoryPath) {
        return new JFileChooser(directoryPath);
    }
}
