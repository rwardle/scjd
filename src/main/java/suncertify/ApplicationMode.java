/*
 * ApplicationMode.java
 *
 * 21 Aug 2007 
 */

package suncertify;

/**
 * A mode that the application runs in.
 *
 * @author Richard Wardle
 */
public enum ApplicationMode {

    /**
     * An application that provides a user-interface to a remote server.
     */
    CLIENT,

    /**
     * An application that manages a database and publishes a server that applications running in
     * CLIENT mode can connect to remotely.
     */
    SERVER,

    /**
     * An application that runs locally (with no networking) and provides both user-interface and
     * database management functionality.
     */
    STANDALONE
}
