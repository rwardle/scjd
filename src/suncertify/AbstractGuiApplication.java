/*
 * AbstractGuiApplication.java
 *
 * Created on 07-Jul-2007
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

    /**
     * Creates a new instance of <code>AbstractGuiApplication</code>.
     * 
     * @param configuration The application configuration.
     * @param exceptionHandler The application exception handler.
     * @param shutdownHandler The application shutdown handler.
     * @throws IllegalArgumentException If the any of the 
     * <code>configuration</code>, <code>exceptionHandler</code> or
     * <code>shutdownHandler</code> parameters are <code>null</code>.
     */
    public AbstractGuiApplication(Configuration configuration,
            ExceptionHandler exceptionHandler, 
            ShutdownHandler shutdownHandler) {
        super(configuration, exceptionHandler, shutdownHandler);
    }

    /**
     * {@inheritDoc} 
     * <p/> 
     * Displays the main application window. 
     * <p/> 
     * This implementation calls the <code>createMainPresenter</code> method to
     * obtain the main application presenter.
     */
    public final void startup() throws ApplicationException {
        createMainPresenter().realiseView();
    }

    /**
     * Creates the main application presenter. 
     * <p/>
     * This method is called from the <code>startup</code> method. 
     * <p/> 
     * This implementation calls the <code>getBrokerService</code> method to 
     * get the broker service.
     * 
     * @return The presenter.
     * @throws ApplicationException If the presenter cannot be created.
     */
    protected MainPresenter createMainPresenter() throws ApplicationException {
        MainView mainView = createMainView();
        MainPresenter mainPresenter = new MainPresenter(getBrokerService(),
                mainView);
        mainView.setPresenter(mainPresenter);
        return mainPresenter;
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
    protected abstract BrokerService getBrokerService()
            throws ApplicationException;

    private MainView createMainView() {
        return new MainFrame();
    }

    @Override
    public void shutdown() {
        // TODO Cleanup UI
        super.shutdown();
    }
}
