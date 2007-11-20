/*
 * ServerApplication.java
 *
 * 05 Jul 2007
 */

package suncertify;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import suncertify.db.DataValidationException;
import suncertify.db.DatabaseFactory;
import suncertify.presentation.ConfigurationView;
import suncertify.presentation.ServerConfigurationDialog;
import suncertify.service.RemoteBrokerService;
import suncertify.service.RemoteBrokerServiceImpl;
import suncertify.service.RmiService;

/**
 * The server mode application.
 * 
 * @author Richard Wardle
 */
public final class ServerApplication extends AbstractApplication {

    private static Logger LOGGER = Logger.getLogger(ServerApplication.class
            .getName());
    private final RmiService rmiService;
    private final DatabaseFactory databaseFactory;

    /**
     * Creates a new instance of <code>ServerApplication</code>.
     * 
     * @param configuration
     *                The application configuration.
     * @param rmiService
     *                The RMI service.
     * @param databaseFactory
     *                The database factory.
     * @throws IllegalArgumentException
     *                 If any of the <code>configuration</code>,
     *                 <code>rmiService</code> or <code>databaseFactory</code>
     *                 parameters are <code>null</code>.
     */
    public ServerApplication(Configuration configuration,
            RmiService rmiService, DatabaseFactory databaseFactory) {
        super(configuration);
        this.rmiService = rmiService;
        this.databaseFactory = databaseFactory;
    }

    /** {@inheritDoc} */
    @Override
    protected ConfigurationView createConfigurationView() {
        return new ServerConfigurationDialog();
    }

    /**
     * {@inheritDoc} <p/> Starts the RMI registry and binds the
     * <code>BrokerService</code> object into it.
     */
    public void startup() throws FatalException {
        Integer serverPort = getConfigurationManager().getServerPort();
        String databaseFilePath = getConfigurationManager()
                .getDatabaseFilePath();
        String url = getRemoteBrokerServiceUrl();

        try {
            this.rmiService.createRegistry(serverPort);
            RemoteBrokerService service = new RemoteBrokerServiceImpl(
                    this.databaseFactory.createDatabase(databaseFilePath));
            this.rmiService.rebind(url, service);
        } catch (RemoteException e) {
            throw new FatalException("Error starting RMI on port: "
                    + serverPort, "FatalException.rmiServerError", e);
        } catch (MalformedURLException e) {
            throw new FatalException("Broker service URL is malformed: " + url,
                    "FatalException.rmiServerError", e);
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

        ServerApplication.LOGGER.info("Server running on port " + serverPort);
    }

    private String getRemoteBrokerServiceUrl() {
        return "//" + ApplicationConstants.LOCALHOST_ADDRESS + ":"
                + getConfigurationManager().getServerPort() + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;
    }
}
