/*
 * ServerApplication.java
 *
 * Created on 05-Jul-2005
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
public final class ServerApplication extends AbstractApplication {

    private static Logger logger = Logger.getLogger(
            ServerApplication.class.getName());

    /**
     * Creates a new instance of <code>ServerApplication</code>.
     *
     * @param configuration The application configuration.
     * @throws NullPointerException If the <code>configuration</code> parameter
     * is <code>null</code>.
     */
    public ServerApplication(Configuration configuration) {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     */
    protected ConfigurationView createConfigurationView() {
        return new ServerConfigurationDialog();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Starts the RMI registry and binds the <code>BrokerService</code> object
     * into it.
     */
    public void run() throws ApplicationException {
        String url = "//127.0.0.1:" + getConfiguration().getServerPort() + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;

        try {
            LocateRegistry.createRegistry(
                    Integer.parseInt(getConfiguration().getServerPort()));
            RemoteBrokerService service = new RemoteBrokerServiceImpl(
                    new Data(getConfiguration().getDatabaseFilePath()));
            Naming.rebind(url, service);
        } catch (RemoteException e) {
            throw new ApplicationException(
                    "Failed to export remote object", e);
        } catch (MalformedURLException e) {
            throw new ApplicationException(
                    "The URL used to bind the remote broker service object "
                            + "is malformed: '" + url + "'",
                    e);
        }

        ServerApplication.logger.info("Server running on port "
                + getConfiguration().getServerPort());
    }
}
