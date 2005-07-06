/*
 * ServerApplication.java
 *
 * Created on 05-Jul-2005
 */


package suncertify;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

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
     */
    public ServerApplication() {
        super();
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
    public void run(Configuration configuration) {
        // TODO:

        try {
            LocateRegistry.createRegistry(Integer.parseInt(configuration
                    .getServerPort()));
            RemoteBrokerService service = new RemoteBrokerServiceImpl(
                    configuration.getDatabaseFilePath());
            Naming.rebind("//127.0.0.1:" + configuration.getServerPort() + "/"
                    + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME, service);
        } catch (Exception e) {
            // TODO: Implement proper exception handling
            throw new RuntimeException(e);
        }
    }
}
