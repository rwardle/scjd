/*
 * ClientApplication.java
 *
 * 05 Jul 2007
 */

package suncertify;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import suncertify.presentation.ClientConfigurationDialog;
import suncertify.presentation.ConfigurationView;
import suncertify.service.BrokerService;
import suncertify.service.RmiService;

/**
 * An application that runs in {@link ApplicationMode#CLIENT} mode.
 * 
 * @author Richard Wardle
 */
public final class ClientApplication extends AbstractGuiApplication {

    private final RmiService rmiService;

    /**
     * Creates a new instance of <code>ClientApplication</code>.
     * 
     * @param configuration
     *                Application configuration.
     * @param rmiService
     *                RMI service.
     * @throws IllegalArgumentException
     *                 If <code>configuration</code> or
     *                 <code>rmiService</code> are <code>null</code>.
     */
    public ClientApplication(Configuration configuration, RmiService rmiService) {
        super(configuration);
        this.rmiService = rmiService;
    }

    /**
     * Returns a new configuration view for a client application.
     * 
     * @return The configuration view.
     */
    @Override
    protected ConfigurationView createConfigurationView() {
        return new ClientConfigurationDialog();
    }

    /**
     * Returns a new broker service for a client application.
     * 
     * @return The broker service.
     * @throws FatalException
     *                 If there is an error creating the broker service.
     */
    @Override
    protected BrokerService createBrokerService() throws FatalException {
        String url = "//" + getConfigurationManager().getServerAddress() + ":"
                + getConfigurationManager().getServerPort() + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;

        try {
            return (BrokerService) this.rmiService.lookup(url);
        } catch (MalformedURLException e) {
            throw new FatalException(
                    "The URL used to lookup the remote broker service object "
                            + "is malformed: '" + url + "'",
                    "FatalException.rmiClientError", e);
        } catch (RemoteException e) {
            throw new FatalException(
                    "Error communicating with the remote server",
                    "FatalException.rmiClientError", e);
        } catch (NotBoundException e) {
            throw new FatalException(
                    "Attempted to lookup a name that has not been bound in the "
                            + "RMI registry: '"
                            + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME
                            + "'", "FatalException.rmiClientError", e);
        }
    }
}
