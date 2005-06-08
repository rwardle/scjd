/*
 * Application.java
 *
 * Created on 05-Jun-2005
 */


package suncertify.startup;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * The main application.
 *
 * @author Richard Wardle
 */
public class Application {

    private static Logger logger = Logger
            .getLogger(Application.class.getName());
    private static final String PROPERTIES_FILE_NAME = "suncertify.properties";

    private ApplicationMode mode;

    /**
     * Creates a new Application.
     *
     * @param mode
     *        The application mode.
     */
    public Application(ApplicationMode mode) {
        this.mode = mode;
    }

    /**
     * Configures the application. Loads any existing configuration, presents it
     * to the user for modification and then saves it.
     *
     * @param configuration
     *        The configuration.
     * @return true if the user completed the configuration process, false if
     *         not.
     */
    public boolean configure(Configuration configuration) {
        configuration.loadConfiguration();
        int returnStatus = showConfigurationDialog(configuration);
        if (returnStatus == ConfigurationPresenter.RETURN_OK) {
            try {
                configuration.saveConfiguration();
            } catch (IOException e) {
                logger.log(Level.WARNING,
                        "Unable to save configuration properties file.", e);
                showSaveConfigurationWarning(configuration);
            }

            return true;
        }

        return false;
    }

    /**
     * Shows the configuration dialog.
     *
     * @param configuration
     *        The configuration.
     * @return The dialog return status: 1 if the user clicked OK, 0 if the user
     *         clicked cancel.
     */
    public int showConfigurationDialog(Configuration configuration) {
        // TODO: Add call to factory here 
        final ConfigurationView dialog = ConfigurationViewFactory
                .createConfigurationDialog(this.mode);
        dialog.initialiseComponents();
        
        ConfigurationPresenter presenter = new ConfigurationPresenter(
                configuration, dialog);

        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    dialog.packAndShow();
                }
            });
        } catch (InterruptedException e) {
            logger.log(Level.WARNING,
                    "Configuration dialog thread interrupted", e);
        } catch (InvocationTargetException e) {
            // TODO: Auto-generated catch block
            e.printStackTrace();
        }

        logger.info("Status '" + presenter.getReturnStatus()
                + "' returned from dialog");
        return presenter.getReturnStatus();
    }

    /**
     * Shows the warning dialog if the configuration properties could not be
     * saved.
     *
     * @param configuration
     *        The configuration.
     */
    public void showSaveConfigurationWarning(Configuration configuration) {
        final String message = "Unable to save configuration properties to: '"
                + new File(configuration.getPropertiesFilePath())
                        .getAbsolutePath()
                + "'.\nPlease ensure that you have write permissions to "
                + "this location next time you run the application.";
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(new JFrame(), message,
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }
            });
        } catch (InterruptedException e) {
            logger.log(Level.WARNING,
                    "Save properties error dialog thread interrupted", e);
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Parses the command line flag to get the application mode.
     *
     * @param args 
     * The command line arguments.
     * @return The application mode.
     */
    public static ApplicationMode getModeFromCommandLine(String[] args) {
        if (args == null) {
            logger.severe("Command line argument array is null.");
            throw new NullPointerException("Command line arguments are null.");
        }

        ApplicationMode mode = null;
        
        if (args.length == 0) {
            mode = ApplicationMode.CLIENT;
        } else if (args[0].equals("server")) {
            mode = ApplicationMode.SERVER;
        } else if (args[0].equals("alone")) {
            mode = ApplicationMode.STANDALONE;
        } else {
            logger.severe("Unrecognised command line mode flag: '" + args[0]
                    + "'.");
            throw new IllegalArgumentException("'" + args[0]
                    + "' is not a valid mode flag. If specified, the mode "
                    + "flag must be either 'server' or 'alone'.");
        }

        return mode;
    }

    /**
     * The entry point to the application.
     *
     * @param args
     *        Command line arguments.
     */
    public static void main(String[] args) {
        ApplicationMode mode = getModeFromCommandLine(args);
        logger.info("Running in " + mode + " mode");        
        Application application = new Application(mode);
        
        Configuration configuration = new Configuration(PROPERTIES_FILE_NAME);
        if (!application.configure(configuration)) {
            logger.info("User cancelled configuration dialog, "
                    + "exiting application.");
            System.exit(0);
        }
    }
}
