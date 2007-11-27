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
 * An application launcher. Contains the <code>main</code> method which is the
 * entry point to the application.
 * 
 * @author Richard Wardle
 */
public final class Launcher {

    private static final Logger LOGGER = Logger.getLogger(Launcher.class
            .getName());

    /** The name of the file holding the application configuration. */
    private static final String CONFIG_FILE_NAME = "suncertify.properties";

    /** The factory used to create the application. */
    private final AbstractApplicationFactory applicationFactory;

    /**
     * Creates a new instance of <code>Launcher</code>.
     * 
     * @param applicationFactory
     *                Application factory to be used for creating the
     *                application.
     * @throws IllegalArgumentException
     *                 If <code>applicationFactory</code> is <code>null</code>.
     */
    public Launcher(AbstractApplicationFactory applicationFactory) {
        if (null == applicationFactory) {
            throw new IllegalArgumentException(
                    "applicationFactory cannot be null");
        }
        this.applicationFactory = applicationFactory;
    }

    /**
     * Creates and launches an application. NOTE: This method must be called on
     * the AWT event dispatching thread.
     */
    public void launch() {
        File propertiesFile = new File(Launcher.CONFIG_FILE_NAME);
        Launcher.LOGGER.info("Using configuration file: "
                + propertiesFile.getAbsolutePath());

        Application application = this.applicationFactory
                .createApplication(new PropertiesConfiguration(propertiesFile));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Launcher.LOGGER.log(Level.WARNING,
                    "Couldn't set system look and feel", e);
        }

        if (application.initialise()) {
            try {
                application.startup();
            } catch (FatalException e) {
                application.handleFatalException(e);
            }
        }
    }

    /**
     * Entry point to the application.
     * 
     * @param args
     *                Command line arguments.
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new FatalExceptionHandler());

        ApplicationMode applicationMode = Launcher.getApplicationMode(args);
        Launcher.LOGGER.info("Running in application mode: " + applicationMode);

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
     * Returns the application mode corresponding to the specified commmand line
     * arguments.
     * 
     * @param args
     *                Command line arguments.
     * @return The application mode.
     * @throws IllegalArgumentException
     *                 If <code>args</code> is <code>null</code>, or
     *                 <code>args</code> contains an invalid application mode
     *                 flag.
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
            throw new IllegalArgumentException(
                    "Invalid application mode flag: " + args[0]
                            + ". Flag must be either 'server' or 'alone'");
        }
        return mode;
    }
}
