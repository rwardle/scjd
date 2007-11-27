/*
 * AbstractApplication.java
 *
 * 05 Jun 2007
 */

package suncertify;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import suncertify.presentation.ConfigurationPresenter;
import suncertify.presentation.ConfigurationView;

/**
 * An abstract base class for applications.
 * 
 * @author Richard Wardle
 */
public abstract class AbstractApplication implements Application {

    private static final Logger LOGGER = Logger
            .getLogger(AbstractApplication.class.getName());

    private final ConfigurationManager configurationManager;
    private final FatalExceptionHandler exceptionHandler;
    private final ResourceBundle resourceBundle;

    /**
     * Creates a new instance of <code>AbstractApplication</code>.
     * 
     * @param configuration
     *                Application configuration.
     * @throws IllegalArgumentException
     *                 If <code>configuration</code> is <code>null</code>.
     */
    public AbstractApplication(Configuration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must be non-null");
        }

        this.configurationManager = new ConfigurationManager(configuration);
        this.exceptionHandler = new FatalExceptionHandler();
        this.resourceBundle = ResourceBundle.getBundle("suncertify/Bundle");
    }

    /**
     * Returns the configuration manager.
     * 
     * @return The configuration manager.
     */
    protected final ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Displays a configuration dialog.
     * <p>
     * This implementation calls the <code>createConfigurationView</code>
     * method.
     */
    public final boolean initialise() {
        ReturnStatus returnStatus = showConfigurationDialog();

        boolean initialised;
        if (returnStatus == ReturnStatus.OK) {
            initialised = true;

            try {
                AbstractApplication.LOGGER.info("Saving configuration to disk");
                getConfigurationManager().save();
            } catch (ConfigurationException e) {
                AbstractApplication.LOGGER.log(Level.WARNING,
                        "Could not save configuration to disk", e);
                showSaveWarningDialog();
            }
        } else {
            initialised = false;
            AbstractApplication.LOGGER
                    .info("Configuration cancelled by user, exiting application");
        }

        return initialised;
    }

    private ReturnStatus showConfigurationDialog() {
        ConfigurationPresenter presenter = createConfigurationPresenter();
        presenter.realiseView();
        ReturnStatus returnStatus = presenter.getReturnStatus();
        AbstractApplication.LOGGER
                .info("Returned from configuration dialog with status: "
                        + returnStatus);
        return returnStatus;
    }

    ConfigurationPresenter createConfigurationPresenter() {
        ConfigurationView view = createConfigurationView();
        ConfigurationPresenter presenter = new ConfigurationPresenter(
                getConfigurationManager(), view);
        view.setPresenter(presenter);
        return presenter;
    }

    /**
     * Returns a new configuration view.
     * <p>
     * This method is called by the <code>initialise</code> method. Subclasses
     * should implement this method to return a configuration view that is
     * application-specific.
     * 
     * @return The configuration view.
     */
    protected abstract ConfigurationView createConfigurationView();

    void showSaveWarningDialog() {
        String message = this.resourceBundle
                .getString("AbstractApplication.saveConfigurationWarningDialog.message");
        String title = this.resourceBundle
                .getString("AbstractApplication.saveConfigurationWarningDialog.title");
        JOptionPane.showMessageDialog(null, message, title,
                JOptionPane.WARNING_MESSAGE);
    }

    /** {@inheritDoc} */
    public final void handleFatalException(FatalException exception) {
        this.exceptionHandler.handleException(exception);
    }
}
