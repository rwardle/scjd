/*
 * FatalExceptionHandler.java
 *
 * 8 Nov 2007 
 */

package suncertify;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

/**
 * An exception handler for handling fatal and uncaught exceptions. Displays an
 * error dialog to the user and logs the exception.
 * 
 * @author Richard Wardle
 */
public final class FatalExceptionHandler implements
        Thread.UncaughtExceptionHandler {

    private static final Logger LOGGER = Logger
            .getLogger(FatalExceptionHandler.class.getName());

    /** Resource bundle for looking-up user-friendly error messages. */
    private final ResourceBundle resourceBundle;

    /** Error dialog title. */
    private final String title;

    /** Error dialog message pattern. */
    private final String pattern;

    /** Error dialog default message. */
    private final String defaultMessage;

    /**
     * Creates a new instance of <code>FatalExceptionHandler</code>.
     */
    public FatalExceptionHandler() {
        this.resourceBundle = ResourceBundle.getBundle("suncertify/Bundle");
        this.title = this.resourceBundle
                .getString("FatalExceptionHandler.title");
        this.pattern = this.resourceBundle
                .getString("FatalExceptionHandler.pattern");
        this.defaultMessage = this.resourceBundle
                .getString("FatalExceptionHandler.defaultMessage");
    }

    /**
     * Handles the specified fatal exception.
     * 
     * @param exception
     *                Fatal exception.
     */
    public void handleException(FatalException exception) {
        FatalExceptionHandler.LOGGER.log(Level.SEVERE,
                "Handling fatal exception", exception);
        String messageKey = exception.getMessageKey();

        String message;
        if (messageKey == null) {
            message = this.defaultMessage;
        } else {
            message = this.resourceBundle.getString(messageKey);
        }

        showDialog(message);
    }

    /** {@inheritDoc} */
    public void uncaughtException(Thread thread, Throwable exception) {
        FatalExceptionHandler.LOGGER.log(Level.SEVERE,
                "Handling uncaught exception", exception);
        showDialog(this.defaultMessage);
    }

    private void showDialog(String message) {
        JOptionPane.showMessageDialog(null, MessageFormat.format(this.pattern,
                message), this.title, JOptionPane.ERROR_MESSAGE);
    }
}
