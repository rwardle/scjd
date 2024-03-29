/*
 * ServerApplication.java
 *
 * 05 Jul 2007
 */

package suncertify;

import suncertify.db.DataValidationException;
import suncertify.db.DatabaseFactory;
import suncertify.presentation.ConfigurationView;
import suncertify.presentation.ServerConfigurationDialog;
import suncertify.service.RemoteBrokerService;
import suncertify.service.RemoteBrokerServiceImpl;
import suncertify.service.RmiService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

/**
 * An application that runs in {@link ApplicationMode#SERVER SERVER} mode.
 *
 * @author Richard Wardle
 */
public final class ServerApplication extends AbstractApplication {

    private final RmiService rmiService;
    private final DatabaseFactory databaseFactory;

    /**
     * Creates a new instance of <code>ServerApplication</code>.
     *
     * @param configuration   Application configuration.
     * @param rmiService      RMI service.
     * @param databaseFactory Database factory.
     * @throws IllegalArgumentException If <code>configuration</code>, <code>rmiService</code> or
     *                                  <code>databaseFactory</code> is <code>null</code>.
     */
    public ServerApplication(Configuration configuration, RmiService rmiService,
                             DatabaseFactory databaseFactory) {
        super(configuration);

        if (rmiService == null) {
            throw new IllegalArgumentException("rmiService cannot be null");
        }
        if (databaseFactory == null) {
            throw new IllegalArgumentException("databaseFactory cannot be null");
        }
        this.rmiService = rmiService;
        this.databaseFactory = databaseFactory;
    }

    /**
     * Returns a new configuration view for a server application.
     *
     * @return The configuration view.
     */
    @Override
    protected ConfigurationView createConfigurationView() {
        return new ServerConfigurationDialog();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Creates a database and a new remote broker service object that uses the database. This remote
     * object is bound into an RMI registry.
     */
    public void startup() throws FatalException {
        Integer serverPort = getConfigurationManager().getServerPort();
        String url = "//" + ApplicationConstants.LOCALHOST_ADDRESS + ":" + serverPort + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;
        String databaseFilePath = getConfigurationManager().getDatabaseFilePath();

        try {
            rmiService.createRegistry(serverPort);
            RemoteBrokerService service = new RemoteBrokerServiceImpl(
                    databaseFactory.createDatabase(databaseFilePath));
            rmiService.rebind(url, service);
        } catch (RemoteException e) {
            throw new FatalException("Error starting RMI on port: " + serverPort,
                    "FatalException.rmiServerError.message", e);
        } catch (MalformedURLException e) {
            throw new FatalException("Broker service URL is malformed: " + url,
                    "FatalException.rmiServerError.message", e);
        } catch (FileNotFoundException e) {
            throw new FatalException("Could not create database: file not found",
                    "FatalException.databaseFileNotFound.message", e);
        } catch (DataValidationException e) {
            throw new FatalException("Could not create database: invalid database file",
                    "FatalException.databaseInvalid.message", e);
        } catch (IOException e) {
            throw new FatalException("Could not create database: error reading database file",
                    "FatalException.databaseReadError.message", e);
        }
    }
}
