/*
 * ClientApplication.java
 *
 * Created on 05-Jul-2005
 */


package suncertify;

import java.rmi.Naming;

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
    protected ConfigurationView createConfigurationView() {
        return new ClientConfigurationDialog();
    }

    /**
     * {@inheritDoc}
     */
    protected BrokerService getBrokerService() {
        try {
            return (BrokerService) Naming.lookup("//"
                    + getConfiguration().getServerAddress() + ":"
                    + getConfiguration().getServerPort() + "/"
                    + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME);
        } catch (Exception e) {
            // TODO: Implement proper exception handling
            throw new RuntimeException(e);
        }
    }
}
