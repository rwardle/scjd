/*
 * Main.java
 *
 * Created on 05-Jul-2005
 */


package suncertify;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The starting class for the application.
 *
 * @author Richard Wardle
 */
public final class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * Creates a new instance of <code>Main</code>.
     */
    public Main() {
        super();
    }

    /**
     * Gets the application mode based on the command-line arguments.
     *
     * @param args The command-line arguments.
     * @return The application mode.
     * @throws NullPointerException If the command-line arguments are null.
     * @throws IllegalArgumentException If the command-line arguments are
     * invalid.
     */
    public ApplicationMode getApplicationMode(String[] args) {
        if (args == null) {
            String message = "Command line argument array is null";
            Main.logger.severe(message);
            throw new NullPointerException(message);
        }

        ApplicationMode mode;
        if (args.length == 0) {
            mode = ApplicationMode.CLIENT;
        } else if (args[0].equals("server")) {
            mode = ApplicationMode.SERVER;
        } else if (args[0].equals("alone")) {
            mode = ApplicationMode.STANDALONE;
        } else {
            Main.logger.severe("Unrecognised command line mode flag: '"
                    + args[0] + "'");
            throw new IllegalArgumentException("'" + args[0]
                    + "' is not a valid mode flag. If specified, the mode "
                    + "flag must be either 'server' or 'alone'.");
        }

        return mode;
    }

    /**
     * Simple factory method that creates an application based on the supplied
     * application mode.
     *
     * @param mode The application mode.
     * @param configuration The application configuration.
     * @return The application.
     * @throws NullPointerException If the <code>configuration</code> parameter
     * is <code>null</code>.
     */
    public Application createApplication(ApplicationMode mode,
            Configuration configuration) {
        Application application = null;

        if (mode == ApplicationMode.CLIENT) {
            application = new ClientApplication(configuration);
        } else if (mode == ApplicationMode.SERVER) {
            application = new ServerApplication(configuration);
        } else if (mode == ApplicationMode.STANDALONE) {
            application = new StandaloneApplication(configuration);
        } else {
            assert false : mode;
        }

        return application;
    }

    /**
     * Configures the supplied application.
     *
     * @param application The application to configure.
     * @param propertiesFilePath The path to the properties file.
     * @throws NullPointerException If the <code>application</code> or the
     * <code>propertiesFilePath</code> parameter is <code>null</code>.
     */
    public void configureApplication(Application application,
            String propertiesFilePath) {
        if (application == null || propertiesFilePath == null) {
            throw new NullPointerException("The application and "
                    + "propertiesFilePath parameters must be non-null");
        }

        File propertiesFile = new File(propertiesFilePath);
        Main.logger.info("Properties file path: '"
                + propertiesFile.getAbsolutePath() + "'");

        try {
            if (!application.configure(propertiesFile)) {
                Main.logger.info(
                        "User cancelled configuration, exiting application");
                application.exit(0);
            }
        } catch (ApplicationException e) {
            Main.logger.log(Level.SEVERE,
                    "Unable to read/write to properties file at: '"
                            + propertiesFile.getAbsolutePath()
                            + "', exiting application",
                     e);
            // TODO: Make this message briefer
            String message = "Unable to read/write to properties file at: '"
                    + propertiesFile.getAbsolutePath()
                    + "'.\nPlease ensure that you set the correct file "
                    + "permissions for this location.\nThe application will "
                    + "now exit.";

            try {
                application.showErrorDialog(message);
            } catch (ApplicationException e1) {
                // There was an error showing the error dialog so output the
                // error message to standard error before exiting
                Main.logger.log(Level.SEVERE, "Error displaying error dialog",
                        e1);
                System.err.println(message);
            }

            application.exit(1);
        }
    }

    /**
     * Runs the supplied application.
     *
     * @param application The application to run.
     * @throws NullPointerException If the <code>application</code> parameter is
     * <code>null</code>.
     */
    public void runApplication(Application application) {
        if (application == null) {
            throw new NullPointerException("Application must be non-null");
        }

        try {
            application.run();
        } catch (ApplicationException e) {
            Main.logger.log(Level.SEVERE, "Error running application", e);
            // TODO: Revise this message
            String message = "Error running application: '" + e.getMessage()
                    + "'.\nThe application will now exit.";
            try {
                application.showErrorDialog(message);
            } catch (ApplicationException e1) {
                // There was an error showing the error dialog so output the
                // error message to standard error before exiting
                Main.logger.log(Level.SEVERE, "Error displaying error dialog",
                        e1);
                System.err.println(message);
            }

            application.exit(1);
        }
    }

    /**
     * The starting method for the application.
     *
     * @param args Command line arguments.
     * @throws NullPointerException If the command-line arguments are null.
     * @throws IllegalArgumentException If the command-line arguments are
     * invalid.
     */
    public static void main(String[] args) {
        Main main = new Main();

        ApplicationMode mode = main.getApplicationMode(args);
        Main.logger.info("Runnng in application mode: " + mode);
        Configuration configuration = new Configuration(
                ApplicationConstants.DEFAULT_PROPERTIES);
        Application application = main.createApplication(mode, configuration);

        main.configureApplication(application, "suncertify.properties");
        main.runApplication(application);
    }
}
