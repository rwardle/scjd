/*
 * StandaloneApplication.java
 *
 * Created on 05-Jul-2005
 */


package suncertify;

import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import suncertify.presentation.ConfigurationView;
import suncertify.presentation.StandaloneConfigurationDialog;
import suncertify.service.BrokerService;
import suncertify.service.BrokerServiceImpl;


/**
 * The standalone mode application.
 *
 * @author Richard Wardle
 */
public final class StandaloneApplication extends AbstractApplication {

    /**
     * Creates a new instance of <code>StandaloneApplication</code>.
     */
    public StandaloneApplication() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected ConfigurationView createConfigurationView() {
        return new StandaloneConfigurationDialog();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Displays the main application window.
     */
    public void run(final Configuration configuration) {
        // TODO:

        final BrokerService service = new BrokerServiceImpl(configuration
                .getDatabaseFilePath());

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                try {
                    frame.setTitle(service.getHelloWorld());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
