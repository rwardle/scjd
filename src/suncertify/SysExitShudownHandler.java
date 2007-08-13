/*
 * SysExitShudownHandler.java
 *
 * 05 Jul 2007
 */

package suncertify;

public class SysExitShudownHandler implements ShutdownHandler {

    public void handleShutdown() {
        System.exit(0);
    }
}
