/*
 * AbstractApplication.java
 *
 * Created on 05-Jun-2007
 */

package suncertify;

import java.util.logging.Logger;
import suncertify.presentation.ConfigurationPresenter;
import suncertify.presentation.ConfigurationView;

/**
 * The abstract base class for the application.
 * 
 * @author Richard Wardle
 */
public abstract class AbstractApplication implements Application {

    private static final Logger LOGGER 
            = Logger.getLogger(AbstractApplication.class.getName());
    private final ConfigurationManager configurationManager;
    private final ExceptionHandler exceptionHandler;
    private final ShutdownHandler shutdownHandler;

    /**
     * Creates a new instance of <code>AbstractApplication</code>.
     * 
     * @param configuration The application configuration.
     * @param exceptionHandler The application exception handler.
     * @param shutdownHandler The application shutdown handler.
     * @throws IllegalArgumentException If the any of the 
     * <code>configuration</code>, <code>exceptionHandler</code> or
     * <code>shutdownHandler</code> parameters are <code>null</code>.
     */
    public AbstractApplication(Configuration configuration,
            ExceptionHandler exceptionHandler, ShutdownHandler shutdownHandler) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must be non-null");
        }
        if (exceptionHandler == null) {
            throw new IllegalArgumentException(
                    "exceptionHandler must be non-null");
        }
        if (shutdownHandler == null) {
            throw new IllegalArgumentException(
                    "shutdownHandler must be non-null");
        }
        
        this.configurationManager = new ConfigurationManager(configuration);
        this.exceptionHandler = exceptionHandler;
        this.shutdownHandler = shutdownHandler;
    }

    /**
     * Gets the configuration manager.
     * 
     * @return The configuration manager.
     */
    protected final ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    /** {@inheritDoc} */
    public final void initialise() throws ApplicationException {
        ReturnStatus returnStatus = showConfigurationDialog();
        switch (returnStatus) {
        case CANCEL:
            AbstractApplication.LOGGER.info(
                    "User cancelled configuration, exiting application");
            shutdown();
            break;
        case OK:
            try {
                getConfigurationManager().save();
            }
            catch (ConfigurationException e) {
                handleException(new ApplicationException(
                        "Error saving configuration", e));
            }
            break;
        default:
            assert false : returnStatus;
            break;
        }
    }

    private ReturnStatus showConfigurationDialog() {
        ConfigurationPresenter presenter = createConfigurationPresenter();
        presenter.realiseView();
        ReturnStatus returnStatus = presenter.getReturnStatus();
        AbstractApplication.LOGGER.info("Status '" + returnStatus
                + "' returned from dialog");
        return returnStatus;
    }

    ConfigurationPresenter createConfigurationPresenter() {
        ConfigurationView view = createConfigurationView();
        ConfigurationPresenter presenter 
                = new ConfigurationPresenter(getConfigurationManager(), view);
        view.setPresenter(presenter);
        return presenter;
    }

    /**
     * Creates the configuration view. <p/> This method is called by the
     * <code>createConfigurationPresenter</code> method.
     * 
     * @return The view.
     */
    protected abstract ConfigurationView createConfigurationView();

    /** {@inheritDoc} */
    public final void handleException(ApplicationException exception) {
        this.exceptionHandler.handleException(exception);
    }

    /** {@inheritDoc} */
    public void shutdown() {
        this.shutdownHandler.handleShutdown();
    }
}
