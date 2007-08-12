package suncertify;

public class SysExitShudownHandler implements ShutdownHandler {

    public void handleShutdown() {
        System.exit(0);
    }
}
