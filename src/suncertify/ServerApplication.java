/*
 * ServerApplication.java
 *
 * Created on 05-Jul-2005
 */


package suncertify;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import suncertify.service.RemoteBrokerService;
import suncertify.service.RemoteBrokerServiceImpl;
import suncertify.startup.ConfigurationView;
import suncertify.startup.ServerConfigurationDialog;


/**
 * The server version of the application.
 *
 * @author Richard Wardle
 */
public final class ServerApplication extends AbstractApplication {

    /**
     * Creates a new ServerApplication.
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
    public void execute(Configuration configuration) {
        try {
            LocateRegistry.createRegistry(Integer.parseInt(configuration
                    .getServerPort()));
            RemoteBrokerService service = new RemoteBrokerServiceImpl(
                    configuration.getDatabaseFilePath());
            Naming.rebind("//127.0.0.1:" + configuration.getServerPort() + "/"
                    + AbstractApplication.REMOTE_BROKER_SERVICE_NAME, service);
        } catch (Exception e) {
            // TODO: Implement proper exception handling
            throw new RuntimeException(e);
        }
    }
}
