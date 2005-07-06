/*
 * AbstractApplication.java
 *
 * Created on 05-Jun-2005
 */


package suncertify.startup;

import java.awt.EventQueue;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * The abstract base class for the application.
 *
 * @author Richard Wardle
 */
public abstract class AbstractApplication {

    /**
     * The name under which the remote service object is regstered with RMI.
     */
    public static final String REMOTE_BROKER_SERVICE_NAME = "BrokerService";

    private static Logger logger = Logger
            .getLogger(AbstractApplication.class.getName());

    /**
     * Creates a new instance of <code>AbstractApplication</code>.
     */
    public AbstractApplication() {
        super();
    }

    /**
     * Configures the application. Loads any existing configuration, presents it
     * to the user for modification and then saves it.
     * <p/>
     * This implementation indirectly calls the
     * <code>createConfigurationView</code> method to get the configuration view
     * to display to the user.
     *
     * @param configuration The configuration.
     * @return true if the user completed the configuration process, false if
     * not.
     */
    public final boolean configure(Configuration configuration) {
        configuration.loadConfiguration();
        int returnStatus = showConfigurationDialog(configuration);
        if (returnStatus == ConfigurationPresenter.RETURN_OK) {
            try {
                configuration.saveConfiguration();
            } catch (IOException e) {
                AbstractApplication.logger.log(Level.WARNING,
                        "Unable to save configuration properties file at: '"
                                + configuration.getPropertiesFilePath() + "'",
                        e);
                showSaveConfigurationWarning(configuration
                        .getPropertiesFilePath());
            }

            return true;
        }

        return false;
    }

    private int showConfigurationDialog(Configuration configuration) {
        final ConfigurationView dialog = createConfigurationView();
        dialog.initialiseComponents();

        ConfigurationPresenter presenter = new ConfigurationPresenter(
                configuration, dialog);

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

    /**
     * Runs the main application.
     *
     * @param configuration The application configuration.
     */
    public abstract void execute(Configuration configuration);
}
