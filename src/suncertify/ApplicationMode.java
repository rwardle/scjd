/*
 * ApplicationMode.java
 *
 * Created on 08 June 2005
 */

package suncertify;

/**
 * Represents the run mode of the application.
 *
 * @author Richard Wardle
 */
public class ApplicationMode {

    /**
     * Client mode: the GUI runs and connects to the database server over the
     * network.
     */
    public static final ApplicationMode CLIENT = new ApplicationMode("client");

    /**
     * Server mode: the server exposes database functionality to clients over
     * the network.
     */
    public static final ApplicationMode SERVER = new ApplicationMode("server");

    /**
     * Standalone mode: the GUI runs and connects to the database locally.
     */
    public static final ApplicationMode STANDALONE =
            new ApplicationMode("standalone");

    private final String name;

    private ApplicationMode(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return this.name;
    }
}
