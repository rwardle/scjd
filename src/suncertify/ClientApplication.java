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
     */
    public ClientApplication() {
        super();
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
    protected BrokerService getBrokerService(Configuration configuration) {
        try {
            return (BrokerService) Naming.lookup("//"
                    + configuration.getServerAddress() + ":"
                    + configuration.getServerPort() + "/"
                    + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME);
        } catch (Exception e) {
            // TODO: Implement proper exception handling
            throw new RuntimeException(e);
        }
    }
}
