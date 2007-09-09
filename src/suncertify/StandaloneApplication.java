/*
 * StandaloneApplication.java
 *
 * 05 Jul 2007
 */

package suncertify;

import java.io.FileNotFoundException;

import suncertify.db.Data;
import suncertify.db.DataAccessException;
import suncertify.db.DataValidationException;
import suncertify.db.DatabaseFileImpl;
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
     * @param configuration
     *                The application configuration.
     * @param exceptionHandler
     *                The application exception handler.
     * @param shutdownHandler
     *                The application shutdown handler.
     * @throws IllegalArgumentException
     *                 If the any of the <code>configuration</code>,
     *                 <code>exceptionHandler</code> or
     *                 <code>shutdownHandler</code> parameters are
     *                 <code>null</code>.
     */
    public StandaloneApplication(Configuration configuration,
            ExceptionHandler exceptionHandler, ShutdownHandler shutdownHandler) {
        super(configuration, exceptionHandler, shutdownHandler);
    }

    /** {@inheritDoc} */
    @Override
    protected ConfigurationView createConfigurationView() {
        return new StandaloneConfigurationDialog();
    }

    /** {@inheritDoc} */
    @Override
    protected BrokerService createBrokerService() throws ApplicationException {
        try {
            return new BrokerServiceImpl(new Data(new DatabaseFileImpl(
                    getConfigurationManager().getDatabaseFilePath())));
        } catch (FileNotFoundException e) {
            throw new ApplicationException("Database file not found", e);
        } catch (DataAccessException e) {
            throw new ApplicationException("Error reading database file", e);
        } catch (DataValidationException e) {
            throw new ApplicationException("Invalid database file", e);
        }
    }
}
