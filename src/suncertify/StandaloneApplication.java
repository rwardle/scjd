/*
 * StandaloneApplication.java
 *
 * 05 Jul 2007
 */

package suncertify;

import suncertify.db.DataValidationException;
import suncertify.db.DatabaseFactory;
import suncertify.presentation.ConfigurationView;
import suncertify.presentation.StandaloneConfigurationDialog;
import suncertify.service.BrokerService;
import suncertify.service.BrokerServiceImpl;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * An application that runs in {@link ApplicationMode#STANDALONE STANDALONE}
 * mode.
 *
 * @author Richard Wardle
 */
public final class StandaloneApplication extends AbstractGuiApplication {

    private final DatabaseFactory databaseFactory;

    /**
     * Creates a new instance of <code>StandaloneApplication</code>.
     *
     * @param configuration   Application configuration.
     * @param databaseFactory Database factory.
     * @throws IllegalArgumentException If <code>configuration</code> or
     *                                  <code>databaseFactory</code> is <code>null</code>.
     */
    public StandaloneApplication(Configuration configuration,
                                 DatabaseFactory databaseFactory) {
        super(configuration);

        if (databaseFactory == null) {
            throw new IllegalArgumentException("databaseFactory cannot be null");
        }
        this.databaseFactory = databaseFactory;
    }

    /**
     * Returns a new configuration view for a standalone application.
     *
     * @return The configuration view.
     */
    @Override
    protected ConfigurationView createConfigurationView() {
        return new StandaloneConfigurationDialog();
    }

    /**
     * Creates a database and returns a new broker service that uses the
     * database.
     *
     * @return The broker service.
     * @throws FatalException If there is an error creating the broker service.
     */
    @Override
    protected BrokerService createBrokerService() throws FatalException {
        try {
            return new BrokerServiceImpl(databaseFactory
                    .createDatabase(getConfigurationManager()
                            .getDatabaseFilePath()));
        } catch (FileNotFoundException e) {
            throw new FatalException(
                    "Could not create database: file not found",
                    "FatalException.databaseFileNotFound.message", e);
        } catch (DataValidationException e) {
            throw new FatalException(
                    "Could not create database: invalid database file",
                    "FatalException.databaseInvalid.message", e);
        } catch (IOException e) {
            throw new FatalException(
                    "Could not create database: error reading database file",
                    "FatalException.databaseReadError.message", e);
        }
    }
}
