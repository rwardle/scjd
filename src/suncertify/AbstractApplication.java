/*
 * AbstractApplication.java
 *
 * Created on 05-Jun-2005
 */


package suncertify;

import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import suncertify.presentation.ConfigurationPresenter;
import suncertify.presentation.ConfigurationView;


/**
 * The abstract base class for the application.
 *
 * @author Richard Wardle
 */
public abstract class AbstractApplication implements Application {

    private static Logger logger = Logger
            .getLogger(AbstractApplication.class.getName());

    private Configuration configuration;

    /**
     * Creates a new instance of <code>AbstractApplication</code>.
     *
     * @param configuration The application configuration.
     * @throws NullPointerException If the <code>configuration</code> parameter
     * is <code>null</code>.
     */
    public AbstractApplication(Configuration configuration) {
        if (configuration == null) {
            throw new NullPointerException(
                    "The configuration parameter must be non-null");
        }

        this.configuration = configuration;
    }

    /**
     * Gets the configuration.
     *
     * @return The configuration.
     */
    protected final Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation calls the <code>createConfigurationPresenter</code>
     * method to get the configuration presenter.
     */
    public final boolean configure(File propertiesFile) throws
            ApplicationException {
        if (propertiesFile == null) {
            throw new NullPointerException(
                    "The propertiesFile parameter must be non-null");
        }

        if (propertiesFile.exists()) {
            InputStream in = null;
            try {
                in = new BufferedInputStream(
                        new FileInputStream(propertiesFile));
                getConfiguration().loadConfiguration(in);
            } catch (IOException e) {
                throw new ApplicationException("Error loading configuration",
                        e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        AbstractApplication.logger.log(Level.WARNING,
                                "Error closing input stream", e);
                    }
                }
            }
        }

        if (showConfigurationDialog() == ConfigurationPresenter.RETURN_OK) {
            OutputStream out = null;
            try {
                out = new BufferedOutputStream(
                        new FileOutputStream(propertiesFile));
                getConfiguration().saveConfiguration(out);
            } catch (IOException e) {
                throw new ApplicationException("Error saving configuration", e);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        AbstractApplication.logger.log(Level.WARNING,
                                "Error closing output stream", e);
                    }
                }
            }

            return true;
        }

        return false;
    }

    private int showConfigurationDialog() throws ApplicationException {
        final ConfigurationPresenter presenter = createConfigurationPresenter();
        presenter.initialiseView();
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    presenter.realiseView();
                }
            });
        } catch (InterruptedException e) {
            AbstractApplication.logger.log(Level.WARNING,
                    "Configuration dialog thread interrupted", e);
        } catch (InvocationTargetException e) {
            throw new ApplicationException(
                    "Error in application configuration GUI", e);
        }

        int returnStatus = presenter.getReturnStatus();
        AbstractApplication.logger.info("Status '" + returnStatus
                + "' returned from dialog");
        return returnStatus;
    }

    /**
     * Creates the configuration presenter.
     * <p/>
     * This method is called by the <code>configure</code> method.
     * <p/>
     * This implementation calls the <code>createConfigurationView</code>
     * method to get the configuration view.
     *
     * @return The configuration presenter.
     */
    protected ConfigurationPresenter createConfigurationPresenter() {
        return new ConfigurationPresenter(getConfiguration(),
                createConfigurationView());
    }

    /**
     * Creates the configuration view.
     * <p/>
     * This method is called by the <code>createConfigurationPresenter</code>
     * method.
     *
     * @return The view.
     */
    protected abstract ConfigurationView createConfigurationView();

    /**
     * {@inheritDoc}
     */
    public final void showErrorDialog(final String message) throws
            ApplicationException {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(new JFrame(), message,
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        } catch (InterruptedException e) {
            AbstractApplication.logger.log(Level.WARNING,
                    "Error dialog thread interrupted", e);
        } catch (InvocationTargetException e) {
            throw new ApplicationException("Error showing error dialog", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void exit(int status) {
        System.exit(status);
    }
}
