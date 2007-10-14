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
     * @param exceptionHandler
     *                The application exception handler.
     * @param shutdownHandler
     *                The application shutdown handler.
     * @param rmiService
     * @param databaseFactory
     * @throws IllegalArgumentException
     *                 If the any of the <code>configuration</code>,
     *                 <code>exceptionHandler</code> or
     *                 <code>shutdownHandler</code> parameters are
     *                 <code>null</code>.
     */
    public ServerApplication(Configuration configuration,
            ExceptionHandler exceptionHandler, ShutdownHandler shutdownHandler,
            RmiService rmiService, DatabaseFactory databaseFactory) {
        super(configuration, exceptionHandler, shutdownHandler);
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
    public void startup() throws ApplicationException {
        // TODO Improve exception handling
        String url = getRemoteBrokerServiceUrl();
        try {
            this.rmiService.createRegistry(getConfigurationManager()
                    .getServerPort());
            RemoteBrokerService service = new RemoteBrokerServiceImpl(
                    this.databaseFactory
                            .createDatabase(getConfigurationManager()
                                    .getDatabaseFilePath()));
            this.rmiService.rebind(url, service);
        } catch (RemoteException e) {
            throw new ApplicationException("Failed to export remote object", e);
        } catch (MalformedURLException e) {
            throw new ApplicationException(
                    "The URL used to bind the remote broker service object "
                            + "is malformed: '" + url + "'", e);
        } catch (FileNotFoundException e) {
            throw new ApplicationException("Database file not found", e);
        } catch (DataValidationException e) {
            throw new ApplicationException("Invalid database file", e);
        } catch (IOException e) {
            throw new ApplicationException("Error reading database file", e);
        }

        ServerApplication.LOGGER.info("Server running on port "
                + getConfigurationManager().getServerPort());
    }

    private String getRemoteBrokerServiceUrl() {
        return "//" + ApplicationConstants.LOCALHOST_ADDRESS + ":"
                + getConfigurationManager().getServerPort() + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;
    }
}
