/*
 * Launcher.java
 *
 * 05 Jul 2007
 */

package suncertify;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Launches the application.
 * 
 * @author Richard Wardle
 */
public final class Launcher {

    private static final Logger LOGGER = Logger.getLogger(Launcher.class
            .getName());
    private static final String CONFIG_FILE_NAME = "suncertify.properties";
    private final AbstractApplicationFactory applicationFactory;

    /**
     * Creates a new instance of <code>Launcher</code>.
     * 
     * @param applicationFactory
     *                The application factory.
     */
    public Launcher(AbstractApplicationFactory applicationFactory) {
        if (null == applicationFactory) {
            throw new IllegalArgumentException(
                    "applicationFactory cannot be null");
        }
        this.applicationFactory = applicationFactory;
    }

    /**
     * Launches the application.
     * 
     * @param application
     *                The application to launch.
     */
    public void launch() {
        File propertiesFile = new File(CONFIG_FILE_NAME);
        LOGGER.info("Using configuration file: "
                + propertiesFile.getAbsolutePath());
        final Application application = this.applicationFactory
                .createApplication(new PropertiesConfiguration(propertiesFile));
        setLookAndFeel();
        if (application.initialise()) {
            try {
                application.startup();
            } catch (FatalException e) {
                // TODO Rename to FatalException and
                // handleFatalException?
                application.handleFatalException(e);
            }
        }
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Couldn't set system look and feel", e);
        }
    }

    /**
     * The starting method for the application.
     * 
     * @param args
     *                Command line arguments.
     * @throws IllegalArgumentException
     *                 If the command-line arguments are invalid.
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new FatalExceptionHandler());

        ApplicationMode applicationMode = Launcher.getApplicationMode(args);
        LOGGER.info("Running in application mode: " + applicationMode);

        AbstractApplicationFactory applicationFactory = AbstractApplicationFactory
                .getApplicationFactory(applicationMode);
        final Launcher launcher = new Launcher(applicationFactory);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                launcher.launch();
            }
        });
    }

    /**
     * Gets the application mode corresponding to the supplied commmand line
     * arguments.
     * 
     * @param args
     *                The command line arguments.
     * @return The application mode.
     * @throws IllegalArgumentException
     *                 If the command line arguments are <code>null</code>.
     */
    static ApplicationMode getApplicationMode(String[] args) {
        if (args == null) {
            throw new IllegalArgumentException("args cannot be null");
        }

        ApplicationMode mode;
        if (args.length == 0) {
            mode = ApplicationMode.CLIENT;
        } else if (args[0].equals("server")) {
            mode = ApplicationMode.SERVER;
        } else if (args[0].equals("alone")) {
            mode = ApplicationMode.STANDALONE;
        } else {
            throw new IllegalArgumentException("Invalid mode flag: " + args[0]
                    + ". If specified, the mode flag must be either server "
                    + "or alone.");
        }
        return mode;
    }
}
