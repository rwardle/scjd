/*
 * Launcher.java
 *
 * 05 Jul 2007
 */

package suncertify;

import java.io.File;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Launches the application.
 * 
 * @author Richard Wardle
 */
public final class Launcher {

    static enum ApplicationMode {
        CLIENT, SERVER, STANDALONE
    }

    private static final Logger LOGGER = Logger.getLogger(Launcher.class
            .getName());
    private static final String CONFIG_FILE_NAME = "suncertify.properties";

    /**
     * The starting method for the application.
     * 
     * @param args
     *                Command line arguments.
     * @throws IllegalArgumentException
     *                 If the command-line arguments are invalid.
     */
    public static void main(String[] args) {
        // TODO Consider handlers
        Thread.setDefaultUncaughtExceptionHandler(new SysErrExceptionHandler());
        Launcher launcher = new Launcher();
        ApplicationMode applicationMode = launcher.getApplicationMode(args);
        Launcher.LOGGER.info("Running in application mode: " + applicationMode);
        Application application = launcher.createApplication(applicationMode);
        launcher.launch(application);
    }

    ApplicationMode getApplicationMode(String[] args) {
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
                    + ". If specified, the mode flag must be either 'server' "
                    + "or 'alone'.");
        }
        return mode;
    }

    Application createApplication(ApplicationMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode cannot be null");
        }

        // TODO Consider handlers
        Configuration configuration = new PropertiesConfiguration(new File(
                Launcher.CONFIG_FILE_NAME));
        Application application = null;
        switch (mode) {
        case CLIENT:
            application = new ClientApplication(configuration,
                    new SysErrExceptionHandler(), new SysExitShudownHandler());
            break;
        case SERVER:
            application = new ServerApplication(configuration,
                    new SysErrExceptionHandler(), new SysExitShudownHandler());
            break;
        case STANDALONE:
            application = new StandaloneApplication(configuration,
                    new SysErrExceptionHandler(), new SysExitShudownHandler());
            break;
        default:
            assert false : mode;
            break;
        }
        return application;
    }

    /**
     * Launches the application.
     * 
     * @param application
     *                The application to launch.
     */
    public void launch(final Application application) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    application.initialise();
                    application.startup();
                } catch (ApplicationException e) {
                    application.handleException(e);
                    application.shutdown();
                }
            }
        });
    }
}
