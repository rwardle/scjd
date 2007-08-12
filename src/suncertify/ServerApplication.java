/*
 * ServerApplication.java
 *
 * Created on 05-Jul-2007
 */

package suncertify;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Logger;
import suncertify.db.Data;
import suncertify.presentation.ConfigurationView;
import suncertify.presentation.ServerConfigurationDialog;
import suncertify.service.RemoteBrokerService;
import suncertify.service.RemoteBrokerServiceImpl;

/**
 * The server mode application.
 * 
 * @author Richard Wardle
 */
public final class ServerApplication
        extends AbstractApplication {

    private static Logger LOGGER 
            = Logger.getLogger(ServerApplication.class.getName());

    /**
     * Creates a new instance of <code>ServerApplication</code>.
     * 
     * @param configuration The application configuration.
     * @param exceptionHandler The application exception handler.
     * @param shutdownHandler The application shutdown handler.
     * @throws IllegalArgumentException If the any of the 
     * <code>configuration</code>, <code>exceptionHandler</code> or
     * <code>shutdownHandler</code> parameters are <code>null</code>.
     */
    public ServerApplication(Configuration configuration, 
            ExceptionHandler exceptionHandler, 
            ShutdownHandler shutdownHandler) {
        super(configuration, exceptionHandler, shutdownHandler);
    }

    /** {@inheritDoc} */
    @Override
    protected ConfigurationView createConfigurationView() {
        return new ServerConfigurationDialog();
    }

    /**
     * {@inheritDoc} 
     * <p/> 
     * Starts the RMI registry and binds the <code>BrokerService</code> object 
     * into it.
     */
    public void startup() throws ApplicationException {
        // TODO We're on the EDT here - is that OK?
        String url = "//127.0.0.1:" + getConfigurationManager().getServerPort()
                + "/" + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;

        // TODO Should broker service and data class be created with factory
        // methods or passed in to facilitate testing? Or should the Data 
        // implementation of DBMain be package access to hide it and have a 
        // factory method in the db package to return on unknown implementation
        // of the DBMain interface?
        try {
            LocateRegistry.createRegistry(Integer
                    .parseInt(getConfigurationManager().getServerPort()));
            RemoteBrokerService service = new RemoteBrokerServiceImpl(new Data(
                    getConfigurationManager().getDatabaseFilePath()));
            Naming.rebind(url, service);
        } 
        catch (RemoteException e) {
            throw new ApplicationException("Failed to export remote object", 
                    e);
        } 
        catch (MalformedURLException e) {
            throw new ApplicationException(
                    "The URL used to bind the remote broker service object "
                            + "is malformed: '" + url + "'", 
                    e);
        }

        ServerApplication.LOGGER.info("Server running on port "
                + getConfigurationManager().getServerPort());
    }

    @Override
    public void shutdown() {
        // TODO Shutdown RMI????
        super.shutdown();
    }
}
