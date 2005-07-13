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
     *
     * @param configuration The application configuration.
     * @throws NullPointerException If the <code>configuration</code> parameter
     * is <code>null</code>.
     */
    public AbstractGuiApplication(Configuration configuration) {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Displays the main application window.
     * <p/>
     * This implementation calls the <code>createMainPresenter</code> method to
     * obtain the main application presenter.
     */
    public final void run() throws ApplicationException {
        final MainPresenter presenter = createMainPresenter();
        presenter.initialiseView();

        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    presenter.realiseView();
                }
            });
        } catch (InterruptedException e) {
            AbstractGuiApplication.logger.log(Level.WARNING,
                    "Main frame thread interrupted", e);
        } catch (InvocationTargetException e) {
            throw new ApplicationException("Error in application main GUI", e);
        }
    }

    /**
     * Creates the main application presenter.
     * <p/>
     * This method is called from the <code>run</code> method.
     * <p/>
     * This implementation calls the <code>getBrokerService</code> method to get
     * the broker service.
     *
     * @return The presenter.
     * @throws ApplicationException If the presenter cannot be created.
     */
    protected MainPresenter createMainPresenter() throws ApplicationException {
        return new MainPresenter(getBrokerService(), createMainView());
    }

    /**
     * Gets the broker service.
     * <p/>
     * This method is called from the <code>createMainPresenter</code> method.
     *
     * @return The broker service object.
     * @throws ApplicationException If there is an error getting the broker
     * service.
     */
    protected abstract BrokerService getBrokerService() throws
            ApplicationException;

    private MainView createMainView() {
        return new MainFrame();
    }
}
