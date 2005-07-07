/*
 * AbstractGuiApplication.java
 *
 * Created on 07-Jul-2005
 */


package suncertify;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import suncertify.presentation.MainFrame;
import suncertify.presentation.MainPresenter;
import suncertify.presentation.MainView;
import suncertify.service.BrokerService;


/**
 * Abstract base class for application modes that display a main GUI window.
 *
 * @author Richard Wardle
 */
public abstract class AbstractGuiApplication extends AbstractApplication {

    private static Logger logger = Logger
            .getLogger(AbstractGuiApplication.class.getName());

    /**
     * Creates a new instance of <code>AbstractGuiApplication</code>.
     */
    public AbstractGuiApplication() {
        super();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Displays the main application window.
     * <p/>
     * This implementation calls the <code>getBrokerService</code> method to
     * obtain the broker service object used in the main application.
     */
    public final void run(Configuration configuration) {
        BrokerService brokerService = getBrokerService(configuration);
        final MainView view = new MainFrame();
        view.initialiseComponents();
        new MainPresenter(brokerService, view);

        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    view.realiseView();
                }
            });
        } catch (InterruptedException e) {
            AbstractGuiApplication.logger.log(Level.WARNING,
                    "Main frame thread interrupted", e);
        } catch (InvocationTargetException e) {
            // TODO: Decide on exception handling
            e.printStackTrace();
        }
    }

    /**
     * Gets the broker service using the supplied configuration information.
     * <p/>
     * This method is called from the <code>run</code> method.
     *
     * @param configuration The configuration.
     * @return The broker service object.
     */
    protected abstract BrokerService getBrokerService(
                Configuration configuration);
}
