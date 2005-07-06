/*
 * ClientApplication.java
 *
 * Created on 05-Jul-2005
 */


package suncertify;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import suncertify.service.BrokerService;
import suncertify.startup.ClientConfigurationDialog;
import suncertify.startup.ConfigurationView;


/**
 *
 *
 * @author Richard Wardle
 */
public final class ClientApplication extends AbstractApplication {

    /**
     * Creates a new ClientApplication.
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
    public void execute(Configuration configuration) {
        final BrokerService brokerService = lookupBrokerService(configuration);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                try {

                    frame.setTitle(brokerService.getHelloWorld());
                } catch (IOException e) {
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
                    + AbstractApplication.REMOTE_BROKER_SERVICE_NAME);
        } catch (MalformedURLException e) {
            // TODO: Implement proper exception handling
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            // TODO: Implement proper exception handling
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            // TODO: Implement proper exception handling
            throw new RuntimeException(e);
        }
    }
}
