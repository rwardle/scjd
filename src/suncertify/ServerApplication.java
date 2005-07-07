/*
 * ServerApplication.java
 *
 * Created on 05-Jul-2005
 */


package suncertify;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

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
    public void run() {
        try {
            LocateRegistry.createRegistry(
                    Integer.parseInt(getConfiguration().getServerPort()));
            RemoteBrokerService service = new RemoteBrokerServiceImpl(
                    new Data(getConfiguration().getDatabaseFilePath()));
            Naming.rebind(
                    "//127.0.0.1:" + getConfiguration().getServerPort() + "/"
                            + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME,
                    service);
        } catch (Exception e) {
            // TODO: Implement proper exception handling
            throw new RuntimeException(e);
        }
    }
}
