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
 * Abstract base class for application modes that display a main GUI window.
 * 
 * @author Richard Wardle
 */
public abstract class AbstractGuiApplication extends AbstractApplication {

    /**
     * Creates a new instance of <code>AbstractGuiApplication</code>.
     * 
     * @param configuration
     *                The application configuration.
     * @throws IllegalArgumentException
     *                 If <code>configuration</code> is <code>null</code>.
     */
    public AbstractGuiApplication(Configuration configuration) {
        super(configuration);
    }

    /**
     * {@inheritDoc} <p/> Displays the main application window. <p/> This
     * implementation calls the <code>createMainPresenter</code> method to
     * obtain the main application presenter.
     */
    public final void startup() throws FatalException {
        createMainPresenter().realiseView();
    }

    /**
     * Creates the main application presenter. <p/> This method is called from
     * the <code>startup</code> method. <p/> This implementation calls the
     * <code>getBrokerService</code> method to get the broker service.
     * 
     * @return The presenter.
     * @throws FatalException
     *                 If the presenter cannot be created.
     */
    protected MainPresenter createMainPresenter() throws FatalException {
        MainView mainView = createMainView();
        MainPresenter mainPresenter = new MainPresenter(createBrokerService(),
                mainView);
        mainView.setPresenter(mainPresenter);
        return mainPresenter;
    }

    /**
     * Creates the broker service. <p/> This method is called from the
     * <code>createMainPresenter</code> method.
     * 
     * @return The broker service object.
     * @throws FatalException
     *                 If there is an error getting the broker service.
     */
    protected abstract BrokerService createBrokerService()
            throws FatalException;

    private MainView createMainView() {
        return new MainFrame();
    }
}
