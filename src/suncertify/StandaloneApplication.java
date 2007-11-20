/*
 * StandaloneApplication.java
 *
 * 05 Jul 2007
 */

package suncertify;

import java.io.FileNotFoundException;
import java.io.IOException;

import suncertify.db.DataValidationException;
import suncertify.db.DatabaseFactory;
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

    private final DatabaseFactory databaseFactory;

    /**
     * Creates a new instance of <code>StandaloneApplication</code>.
     * 
     * @param configuration
     *                The application configuration.
     * @param databaseFactory
     *                The database factory.
     * @throws IllegalArgumentException
     *                 If <code>configuration</code> or
     *                 <code>databaseFactory</code> is <code>null</code>.
     */
    public StandaloneApplication(Configuration configuration,
            DatabaseFactory databaseFactory) {
        super(configuration);
        this.databaseFactory = databaseFactory;
    }

    /** {@inheritDoc} */
    @Override
    protected ConfigurationView createConfigurationView() {
        return new StandaloneConfigurationDialog();
    }

    /** {@inheritDoc} */
    @Override
    protected BrokerService createBrokerService() throws FatalException {
        try {
            return new BrokerServiceImpl(this.databaseFactory
                    .createDatabase(getConfigurationManager()
                            .getDatabaseFilePath()));
        } catch (FileNotFoundException e) {
            throw new FatalException(
                    "Could not create database: file not found",
                    "FatalException.databaseFileNotFound", e);
        } catch (DataValidationException e) {
            throw new FatalException(
                    "Could not create database: invalid database file",
                    "FatalException.databaseInvalid", e);
        } catch (IOException e) {
            throw new FatalException(
                    "Could not create database: error reading database file",
                    "FatalException.databaseReadError", e);
        }
    }
}
