package suncertify;

public class SysErrExceptionHandler implements ExceptionHandler, 
        Thread.UncaughtExceptionHandler {

    public void handleException(ApplicationException exception) {
        exception.printStackTrace();
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        exception.printStackTrace();
    }
}
