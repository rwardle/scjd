/*
 * Application.java
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
 * The main application.
 *
 * @author Richard Wardle
 */
public final class Application {

    private static Logger logger = Logger
            .getLogger(Application.class.getName());

    private ApplicationMode mode;

    /**
     * Creates a new instance of Application.
     *
     * @param args The command line arguments.
     * @throws NullPointerException If the command line arguments are null.
     * @throws IllegalArgumentException If the command line mode flag is not
     * recognised.
     */
    public Application(String[] args) {
        parseModeFlag(args);
        Application.logger.info("Running in '" + this.mode + "' mode");
    }

    private void parseModeFlag(String[] args) {
        if (args == null) {
            String message = "Command line argument array is null";
            Application.logger.severe(message);
            throw new NullPointerException(message);
        }

        if (args.length == 0) {
            this.mode = ApplicationMode.CLIENT;
        } else if (args[0].equals("server")) {
            this.mode = ApplicationMode.SERVER;
        } else if (args[0].equals("alone")) {
            this.mode = ApplicationMode.STANDALONE;
        } else {
            Application.logger.severe("Unrecognised command line mode flag: '"
                    + args[0] + "'");
            throw new IllegalArgumentException("'" + args[0]
                    + "' is not a valid mode flag. If specified, the mode "
                    + "flag must be either 'server' or 'alone'.");
        }
    }

    /**
     * Gets the mode.
     *
     * @return The mode.
     */
    public ApplicationMode getMode() {
        return this.mode;
    }

    /**
     * Configures the application. Loads any existing configuration, presents it
     * to the user for modification and then saves it.
     *
     * @param configuration The configuration.
     * @return true if the user completed the configuration process, false if
     * not.
     */
    public boolean configure(Configuration configuration) {
        configuration.loadConfiguration();
        int returnStatus = showConfigurationDialog(configuration);
        if (returnStatus == ConfigurationPresenter.RETURN_OK) {
            try {
                configuration.saveConfiguration();
            } catch (IOException e) {
                Application.logger.log(Level.WARNING,
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

    /**
     * Shows the configuration dialog.
     *
     * @param configuration The configuration.
     * @return The dialog return status: 1 if the user clicked OK, 0 if the user
     * clicked cancel.
     */
    private int showConfigurationDialog(Configuration configuration) {
        final ConfigurationView dialog = ConfigurationViewFactory
                .createConfigurationView(this.mode);
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
            Application.logger.log(Level.WARNING,
                    "Configuration dialog thread interrupted", e);
        } catch (InvocationTargetException e) {
            // TODO: Decide on exception handling
            e.printStackTrace();
        }

        Application.logger.info("Status '" + presenter.getReturnStatus()
                + "' returned from dialog");
        return presenter.getReturnStatus();
    }

    /**
     * Shows a warning dialog explaining that the configuration properties could
     * not be saved.
     *
     * @param path The path to the configuration properties file.
     */
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
            Application.logger.log(Level.WARNING,
                    "Save properties error dialog thread interrupted", e);
        } catch (InvocationTargetException e) {
            // TODO: Decide on appropriate exception handling
            e.printStackTrace();
        }
    }


    /**
     * The entry point to the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Application application = new Application(args);
        Configuration configuration =
                new Configuration("suncertify.properties");
        if (!application.configure(configuration)) {
            Application.logger.info("User cancelled configuration dialog, "
                    + "exiting application");
            System.exit(0);
        }
    }
}
