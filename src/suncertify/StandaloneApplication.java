/*
 * StandaloneApplication.java
 *
 * Created on 05-Jul-2007
 */

package suncertify;

import suncertify.db.Data;
import suncertify.presentation.ConfigurationView;
import suncertify.presentation.StandaloneConfigurationDialog;
import suncertify.service.BrokerService;
import suncertify.service.BrokerServiceImpl;

/**
 * The standalone mode application.
 * 
 * @author Richard Wardle
 */
public final class StandaloneApplication extends AbstractGuiApplication {

    /**
     * Creates a new instance of <code>StandaloneApplication</code>.
     * 
     * @param configuration The application configuration.
     * @param exceptionHandler The application exception handler.
     * @param shutdownHandler The application shutdown handler.
     * @throws IllegalArgumentException If the any of the 
     * <code>configuration</code>, <code>exceptionHandler</code> or
     * <code>shutdownHandler</code> parameters are <code>null</code>.
     */
    public StandaloneApplication(Configuration configuration,
            ExceptionHandler exceptionHandler, 
            ShutdownHandler shutdownHandler) {
        super(configuration, exceptionHandler, shutdownHandler);
    }

    /** {@inheritDoc} */
    @Override
    protected ConfigurationView createConfigurationView() {
        return new StandaloneConfigurationDialog();
    }

    /** {@inheritDoc} */
    @Override
    protected BrokerService getBrokerService() {
        // TODO: If BrokerServiceImpl not singleton should we do something
        // here to prevent multiple instances?
        return new BrokerServiceImpl(
                new Data(getConfigurationManager().getDatabaseFilePath()));
    }
}
