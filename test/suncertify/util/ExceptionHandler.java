package suncertify.util;

import java.lang.Thread.UncaughtExceptionHandler;

public final class ExceptionHandler implements UncaughtExceptionHandler {

    private Throwable exception;

    public void uncaughtException(Thread t, Throwable e) {
        this.exception = e;
    }

    public Throwable getException() {
        return this.exception;
    }
}
