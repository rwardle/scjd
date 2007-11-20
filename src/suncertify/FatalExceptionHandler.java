/*
 * FatalExceptionHandler.java
 *
 * 8 Nov 2007 
 */
package suncertify;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

/**
 * @author Richard Wardle
 */
public class FatalExceptionHandler implements UncaughtExceptionHandler {

    private static final Logger LOGGER = Logger
            .getLogger(FatalExceptionHandler.class.getName());

    private final ResourceBundle resourceBundle;
    private final String title;
    private final String pattern;
    private final String defaultMessage;

    public FatalExceptionHandler() {
        this.resourceBundle = ResourceBundle.getBundle("suncertify/Bundle");
        this.title = this.resourceBundle
                .getString("FatalExceptionHandler.title");
        this.pattern = this.resourceBundle
                .getString("FatalExceptionHandler.pattern");
        this.defaultMessage = this.resourceBundle
                .getString("FatalExceptionHandler.defaultMessage");
    }

    public void handleException(FatalException exception) {
        LOGGER.log(Level.SEVERE, "Handling fatal exception", exception);
        String messageKey = exception.getMessageKey();

        String message;
        if (messageKey == null) {
            message = this.defaultMessage;
        } else {
            message = this.resourceBundle.getString(messageKey);
        }

        showDialog(message);
    }

    /**
     * {@inheritDoc}
     */
    public void uncaughtException(Thread thread, Throwable exception) {
        LOGGER.log(Level.SEVERE, "Handling uncaught exception", exception);
        showDialog(this.defaultMessage);
    }

    private void showDialog(String message) {
        JOptionPane.showMessageDialog(null, MessageFormat.format(this.pattern,
                message), this.title, JOptionPane.ERROR_MESSAGE);
    }
}
