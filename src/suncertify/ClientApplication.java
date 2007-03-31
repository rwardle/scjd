/*
 * ClientApplication.java
 *
 * Created on 05-Jul-2005
 */

package suncertify;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import suncertify.presentation.ClientConfigurationDialog;
import suncertify.presentation.ConfigurationView;
import suncertify.service.BrokerService;

/**
 * The client mode application.
 *
 * @author Richard Wardle
 */
public final class ClientApplication extends AbstractGuiApplication {

    /**
     * Creates a new instance <code>ClientApplication</code>.
     *
     * @param configuration The application configuration.
     * @throws NullPointerException If the <code>configuration</code> parameter
     * is <code>null</code>.
     */
    public ClientApplication(Configuration configuration) {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ConfigurationView createConfigurationView() {
        return new ClientConfigurationDialog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BrokerService getBrokerService() throws ApplicationException {
        String url = "//" + getConfiguration().getServerAddress() + ":"
                + getConfiguration().getServerPort() + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;

        try {
            return (BrokerService) Naming.lookup(url);
        } catch (MalformedURLException e) {
            throw new ApplicationException(
                    "The URL used to lookup the remote broker service object "
                            + "is malformed: '" + url + "'",
                    e);
        } catch (RemoteException e) {
            throw new ApplicationException(
                    "Error communicating with the remote server", e);
        } catch (NotBoundException e) {
            throw new ApplicationException(
                    "Attempted to lookup a name that has not been bound in the "
                            + "RMI registry: '"
                            + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME
                            + "'",
                    e);
        }
    }
}
