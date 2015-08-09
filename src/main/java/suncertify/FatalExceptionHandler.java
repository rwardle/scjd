/*
 * FatalExceptionHandler.java
 *
 * 08 Nov 2007 
 */

package suncertify;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An exception handler for handling fatal and uncaught exceptions. Displays an error dialog to the
 * user and logs the exception.
 *
 * @author Richard Wardle
 */
public final class FatalExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(FatalExceptionHandler.class.getName());

    // Resource bundle for looking-up user-friendly error messages
    private final ResourceBundle resourceBundle;

    // Error dialog title
    private final String title;

    // Error dialog message pattern
    private final String pattern;

    // Error dialog default message
    private final String defaultMessage;

    /**
     * Creates a new instance of <code>FatalExceptionHandler</code>.
     */
    public FatalExceptionHandler() {
        resourceBundle = ResourceBundle.getBundle("suncertify/Bundle");
        title = resourceBundle.getString("FatalExceptionHandler.title");
        pattern = resourceBundle.getString("FatalExceptionHandler.pattern");
        defaultMessage = resourceBundle.getString("FatalExceptionHandler.defaultMessage");
    }

    /**
     * Handles the specified fatal exception.
     *
     * @param exception Fatal exception.
     */
    public void handleException(FatalException exception) {
        LOGGER.log(Level.SEVERE, "Handling fatal exception", exception);
        String messageKey = exception.getMessageKey();

        String message;
        if (messageKey == null) {
            message = defaultMessage;
        } else {
            message = resourceBundle.getString(messageKey);
        }

        showDialog(message);
    }

    /**
     * {@inheritDoc}
     */
    public void uncaughtException(Thread thread, Throwable exception) {
        LOGGER.log(Level.SEVERE, "Handling uncaught exception", exception);
        showDialog(defaultMessage);
    }

    private void showDialog(String message) {
        JOptionPane.showMessageDialog(null, MessageFormat.format(pattern, message), title,
                JOptionPane.ERROR_MESSAGE);
    }
}
