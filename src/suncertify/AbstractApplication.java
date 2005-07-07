/*
 * AbstractApplication.java
 *
 * Created on 05-Jun-2005
 */


package suncertify;

import java.awt.EventQueue;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import suncertify.presentation.ConfigurationPresenter;
import suncertify.presentation.ConfigurationView;


/**
 * The abstract base class for the application.
 *
 * @author Richard Wardle
 */
public abstract class AbstractApplication implements Application {

    private static Logger logger = Logger
            .getLogger(AbstractApplication.class.getName());
    private Configuration configuration;

    /**
     * Creates a new instance of <code>AbstractApplication</code>.
     *
     * @param configuration The application configuration.
     * @throws NullPointerException If the <code>configuration</code> parameter
     * is <code>null</code>.
     */
    public AbstractApplication(Configuration configuration) {
        if (configuration == null) {
            throw new NullPointerException(
                    "configuration parameter must be non-null");
        }

        this.configuration = configuration;
    }

    /**
     * Gets the configuration.
     *
     * @return The configuration.
     */
    protected final Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Loads any existing configuration from persistent storage, presents it to
     * the user for modification, and saves it to persistent storage.
     * <p/>
     * This implementation indirectly calls the
     * <code>createConfigurationView</code> method to get the configuration view
     * to display to the user.
     */
    public final boolean configure() {
        getConfiguration().loadConfiguration();
        int returnStatus = showConfigurationDialog();
        if (returnStatus == ConfigurationPresenter.RETURN_OK) {
            try {
                getConfiguration().saveConfiguration();
            } catch (IOException e) {
                AbstractApplication.logger.log(Level.WARNING,
                        "Unable to save configuration properties file at: '"
                                + getConfiguration().getPropertiesFilePath()
                                + "'",
                        e);
                showSaveConfigurationWarning(getConfiguration()
                        .getPropertiesFilePath());
            }

            return true;
        }

        return false;
    }

    private int showConfigurationDialog() {
        final ConfigurationView dialog = createConfigurationView();
        dialog.initialiseComponents();

        ConfigurationPresenter presenter = new ConfigurationPresenter(
                getConfiguration(), dialog);

        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    dialog.realiseView();
                }
            });
        } catch (InterruptedException e) {
            AbstractApplication.logger.log(Level.WARNING,
                    "Configuration dialog thread interrupted", e);
        } catch (InvocationTargetException e) {
            // TODO: Decide on exception handling
            e.printStackTrace();
        }

        AbstractApplication.logger.info("Status '" + presenter.getReturnStatus()
                + "' returned from dialog");
        return presenter.getReturnStatus();
    }

    /**
     * Creates the configuration view.
     * <p/>
     * This method is called indirectly by the <code>configure</code> method.
     *
     * @return The view.
     */
    protected abstract ConfigurationView createConfigurationView();

    private void showSaveConfigurationWarning(String path) {
        // TODO: Make this message briefer
        final String message = "Unable to save configuration properties to: '"
                + path + "'.\nPlease ensure that you have write permissions to "
                + "this location next time you run the application.";
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(new JFrame(), message,
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }
            });
        } catch (InterruptedException e) {
            AbstractApplication.logger.log(Level.WARNING,
                    "Save properties error dialog thread interrupted", e);
        } catch (InvocationTargetException e) {
            // TODO: Decide on appropriate exception handling
            e.printStackTrace();
        }
    }
}
