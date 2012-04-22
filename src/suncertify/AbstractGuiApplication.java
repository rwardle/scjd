/*
 * AbstractGuiApplication.java
 *
 * 07 Jul 2007
 */

package suncertify;

import suncertify.presentation.MainFrame;
import suncertify.presentation.MainPresenter;
import suncertify.presentation.MainView;
import suncertify.service.BrokerService;

/**
 * An abstract base class for applications that display a main GUI window.
 * 
 * @author Richard Wardle
 */
public abstract class AbstractGuiApplication extends AbstractApplication {

    /**
     * Creates a new instance of <code>AbstractGuiApplication</code>.
     * 
     * @param configuration
     *            Application configuration.
     * @throws IllegalArgumentException
     *             If <code>configuration</code> is <code>null</code>.
     */
    public AbstractGuiApplication(Configuration configuration) {
        super(configuration);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Displays a main application window.
     * <p/>
     * This implementation calls the <code>createBrokerService</code> method.
     */
    public final void startup() throws FatalException {
        createMainPresenter().realiseView();
    }

    MainPresenter createMainPresenter() throws FatalException {
        MainView mainView = createMainView();
        MainPresenter mainPresenter = new MainPresenter(createBrokerService(), mainView);
        mainView.setPresenter(mainPresenter);
        return mainPresenter;
    }

    /**
     * Returns a new broker service.
     * <p/>
     * This method is called from the <code>startup</code> method. Subclasses should implement this
     * method to return a broker service that is application-specific.
     * 
     * @return The broker service.
     * @throws FatalException
     *             If there is an error creating the broker service.
     */
    protected abstract BrokerService createBrokerService() throws FatalException;

    private MainView createMainView() {
        return new MainFrame();
    }
}
