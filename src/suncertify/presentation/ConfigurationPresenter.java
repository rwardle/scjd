/*
 * ConfigurationPresenter.java
 *
 * Created on 06-Jun-2005
 */

package suncertify.presentation;

import suncertify.Configuration;

/**
 * Encapsulates the presentation behaviour of the configuration dialog.
 *
 * @author Richard Wardle
 */
public class ConfigurationPresenter {

    /** Indicates that the Cancel button was clicked in the view. */
    public static final int RETURN_CANCEL = 0;

    /** Indicates that the OK button was clicked in the view. */
    public static final int RETURN_OK = 1;

    private Configuration configuration;
    private ConfigurationView view;
    private int returnStatus;

    /**
     * Creates a new instance of <code>ConfigurationPresenter</code>.
     *
     * @param configuration The configuration model.
     * @param view The configuration view.
     * @throws NullPointerException If the configuration or view is
     * <code>null</code>.
     */
    public ConfigurationPresenter(Configuration configuration,
            ConfigurationView view) {
        if (configuration == null || view == null) {
            throw new NullPointerException(
                    "configuration and view arguments must be non-null");
        }

        this.configuration = configuration;
        this.view = view;
        this.returnStatus = ConfigurationPresenter.RETURN_CANCEL;
    }

    /**
     * Realises the view.
     */
    public void realiseView() {
        loadViewFromModel();
        this.view.realise();
    }

    /**
     * Gets the returnStatus.
     *
     * @return The returnStatus.
     */
    public int getReturnStatus() {
        return this.returnStatus;
    }

    public final void okButtonActionPerformed() {
        this.returnStatus = ConfigurationPresenter.RETURN_OK;
        saveViewToModel();
        this.view.close();
    }

    public final void cancelButtonActionPerformed() {
        this.view.close();
    }

    // TODO: This should be on the EDT
    private void loadViewFromModel() {
        this.view.setDatabaseFilePath(this.configuration.getDatabaseFilePath());
        this.view.setServerAddress(this.configuration.getServerAddress());
        this.view.setServerPort(this.configuration.getServerPort());
    }

    private void saveViewToModel() {
        this.configuration.setDatabaseFilePath(this.view.getDatabaseFilePath());
        this.configuration.setServerAddress(this.view.getServerAddress());
        this.configuration.setServerPort(this.view.getServerPort());
    }
}
