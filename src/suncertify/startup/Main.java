/*
 * Main.java
 *
 * Created on 05-Jul-2005
 */


package suncertify.startup;

import java.util.logging.Logger;


/**
 * The starting point for the application.
 *
 * @author Richard Wardle
 */
public final class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());
    private String[] args;

    /**
     * Creates a new Main.
     *
     * @param args The command-line arguments.
     */
    public Main(String[] args) {
        this.args = args;
    }

    /**
     * Gets the application mode based on the command-line arguments.
     *
     * @return The application mode.
     * @throws NullPointerException If the command-line arguments are null.
     * @throws IllegalArgumentException If the command-line arguments are
     * invalid.
     */
    public ApplicationMode getApplicationMode() {
        if (this.args == null) {
            String message = "Command line argument array is null";
            Main.logger.severe(message);
            throw new NullPointerException(message);
        }

        ApplicationMode mode;
        if (this.args.length == 0) {
            mode = ApplicationMode.CLIENT;
        } else if (this.args[0].equals("server")) {
            mode = ApplicationMode.SERVER;
        } else if (this.args[0].equals("alone")) {
            mode = ApplicationMode.STANDALONE;
        } else {
            Main.logger.severe("Unrecognised command line mode flag: '"
                    + this.args[0] + "'");
            throw new IllegalArgumentException("'" + this.args[0]
                    + "' is not a valid mode flag. If specified, the mode "
                    + "flag must be either 'server' or 'alone'.");
        }

        return mode;
    }

    /**
     * The entry point to the application.
     *
     * @param args Command line arguments.
     * @throws NullPointerException If the command-line arguments are null.
     * @throws IllegalArgumentException If the command-line arguments are
     * invalid.
     */
    public static void main(String[] args) {
        Main main = new Main(args);
        ApplicationMode mode = main.getApplicationMode();

        AbstractApplication application = null;
        if (mode == ApplicationMode.CLIENT) {
            application = new ClientApplication();
        } else if (mode == ApplicationMode.SERVER) {
            application = new ServerApplication();
        } else if (mode == ApplicationMode.STANDALONE) {
            application = new StandaloneApplication();
        } else {
            assert false : mode;
        }

        // The properties file is expected to be in the working directory
        Configuration configuration = new Configuration(
                "suncertify.properties");

        if (!application.configure(configuration)) {
            Main.logger.info("User cancelled configuration dialog, exiting "
                    + "application");
            System.exit(0);
        }

        application.execute(configuration);
    }
}
