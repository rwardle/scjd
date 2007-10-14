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
 * The client mode application.
 * 
 * @author Richard Wardle
 */
public final class ClientApplication extends AbstractGuiApplication {

    private final RmiService rmiService;

    /**
     * Creates a new instance of <code>ClientApplication</code>.
     * 
     * @param configuration
     *                The application configuration.
     * @param exceptionHandler
     *                The application exception handler.
     * @param shutdownHandler
     *                The application shutdown handler.
     * @param rmiService
     * @throws IllegalArgumentException
     *                 If the any of the <code>configuration</code>,
     *                 <code>exceptionHandler</code> or
     *                 <code>shutdownHandler</code> parameters are
     *                 <code>null</code>.
     */
    public ClientApplication(Configuration configuration,
            ExceptionHandler exceptionHandler, ShutdownHandler shutdownHandler,
            RmiService rmiService) {
        super(configuration, exceptionHandler, shutdownHandler);
        this.rmiService = rmiService;
    }

    /** {@inheritDoc} */
    @Override
    protected ConfigurationView createConfigurationView() {
        return new ClientConfigurationDialog();
    }

    /** {@inheritDoc} */
    @Override
    protected BrokerService createBrokerService() throws ApplicationException {
        // TODO Improve exception handling
        String url = "//" + getConfigurationManager().getServerAddress() + ":"
                + getConfigurationManager().getServerPort() + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;

        try {
            return (BrokerService) this.rmiService.lookup(url);
        } catch (MalformedURLException e) {
            throw new ApplicationException(
                    "The URL used to lookup the remote broker service object "
                            + "is malformed: '" + url + "'", e);
        } catch (RemoteException e) {
            throw new ApplicationException(
                    "Error communicating with the remote server", e);
        } catch (NotBoundException e) {
            throw new ApplicationException(
                    "Attempted to lookup a name that has not been bound in the "
                            + "RMI registry: '"
                            + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME
                            + "'", e);
        }
    }
}
