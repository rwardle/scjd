/*
 * ShutdownHandler.java
 *
 * 05 Jul 2007
 */

package suncertify;

/**
 * Defines an application shutdown handler.
 * 
 * @author Richard Wardle
 */
public interface ShutdownHandler {

    /**
     * Handles the application shutdown.
     */
    void handleShutdown();
}
