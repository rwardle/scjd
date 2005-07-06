/*
 * ClientApplication.java
 *
 * Created on 05-Jul-2005
 */


package suncertify;

import java.awt.EventQueue;
import java.io.IOException;
import java.rmi.Naming;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import suncertify.presentation.ClientConfigurationDialog;
import suncertify.presentation.ConfigurationView;
import suncertify.service.BrokerService;


/**
 * The client mode application.
 *
 * @author Richard Wardle
 */
public final class ClientApplication extends AbstractApplication {

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
     * <p/>
     * Looks up the remote <code>BrokerService</code> object in the RMI registry
     * and displays the main application window.
     */
    public void run(Configuration configuration) {
        // TODO: Implement this properly

        final BrokerService brokerService = lookupBrokerService(configuration);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                try {
                    frame.setTitle(brokerService.getHelloWorld());
                } catch (IOException e) {
                    // TODO: Handle this properly
                    e.printStackTrace();
                }

                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private BrokerService lookupBrokerService(Configuration configuration) {
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
