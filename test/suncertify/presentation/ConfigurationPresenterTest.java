/*
 * ConfigurationPresenterTest.java
 *
 * Created on 10 June 2005
 */


package suncertify.presentation;

import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.logging.Logger;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import suncertify.Configuration;


/**
 * Unit tests for {@link ConfigurationPresenterTest}.
 *
 * @author Richard Wardle
 */
public final class ConfigurationPresenterTest extends MockObjectTestCase {

    private static Logger logger = Logger
            .getLogger(ConfigurationPresenterTest.class.getName());

    private ConfigurationPresenter presenter;
    private String databaseFilePath;
    private String serverAddress;
    private String serverPort;
    private Mock mockConfiguration;
    private Mock mockView;

    /**
     * Creates a new instance of <code>ConfigurationPresenterTest</code>.
     */
    public ConfigurationPresenterTest() {
        super();
        this.databaseFilePath = "databaseFilePath";
        this.serverAddress = "serverAddress";
        this.serverPort = "serverPort";
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() {
        this.mockConfiguration = mock(Configuration.class,
                new Class[] {Properties.class},
                new Object[] {new Properties()});
        this.mockView = mock(ConfigurationView.class);
        this.presenter = new ConfigurationPresenter(
                (Configuration) this.mockConfiguration.proxy(),
                (ConfigurationView) this.mockView.proxy());
    }

    /**
     * Should throw <code>NullPointerException</code> when called with a
     * <code>null</code> configuration.
     */
    public void testConstructorDisallowNullConfiguration() {
        try {
            new ConfigurationPresenter(null,
                    (ConfigurationView) this.mockView.proxy());
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            ConfigurationPresenterTest.logger
                    .info("Caught expected NullPointerException: "
                            + e.getMessage());
        }
    }

    /**
     * Should throw <code>NullPointerException</code> when called with a
     * <code>null</code> view.
     */
    public void testConstructorDisallowNullView() {
        try {
            new ConfigurationPresenter(
                    (Configuration) this.mockConfiguration.proxy(), null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            ConfigurationPresenterTest.logger
                    .info("Caught expected NullPointerException: "
                            + e.getMessage());
        }
    }

    /**
     * The return status should be "cancel" after the constructor runs.
     */
    public void testConstructorReturnStatus() {
        assertEquals(
                "Return status comparison,",
                ConfigurationPresenter.RETURN_CANCEL,
                this.presenter.getReturnStatus());
    }

    /**
     * Should intialise the view components, add the listeners and load the
     * model into the view.
     */
    public void testInitialiseView() {
        this.mockView.expects(once()).method("initialiseComponents");
        this.mockView.expects(once()).method("addOkButtonListener")
                .with(isA(ActionListener.class));
        this.mockView.expects(once()).method("addCancelButtonListener")
                .with(isA(ActionListener.class));
        
        this.mockConfiguration.expects(once()).method("getDatabaseFilePath")
                .will(returnValue(this.databaseFilePath));
        this.mockConfiguration.expects(once()).method("getServerAddress")
                .will(returnValue(this.serverAddress));
        this.mockConfiguration.expects(once()).method("getServerPort")
                .will(returnValue(this.serverPort));

        
        this.mockView.expects(once()).method("setDatabaseFilePath")
                .with(eq(this.databaseFilePath));
        this.mockView.expects(once()).method("setServerAddress")
                .with(eq(this.serverAddress));
        this.mockView.expects(once()).method("setServerPort")
                .with(eq(this.serverPort));

        this.presenter.initialiseView();
    }

    /**
     * Should call realise on the view.
     */
    public void testRealiseView() {
        this.mockView.expects(once()).method("realise");
        this.presenter.realiseView();
    }

    /**
     * Should update the model from the view and return status "ok".
     */
    public void testOkButtonActionPerformed() {
        String newDatabaseFilePath = "newDatabaseFilePath";
        String newServerAddress = "newServerAddress";
        String newServerPort = "newServerPort";

        this.mockView.expects(once()).method("getDatabaseFilePath")
                .will(returnValue(newDatabaseFilePath));
        this.mockView.expects(once()).method("getServerAddress")
                .will(returnValue(newServerAddress));
        this.mockView.expects(once()).method("getServerPort")
                .will(returnValue(newServerPort));
        this.mockView.expects(once()).method("close");

        this.mockConfiguration.expects(once()).method("setDatabaseFilePath")
                .with(eq(newDatabaseFilePath));
        this.mockConfiguration.expects(once()).method("setServerAddress")
                .with(eq(newServerAddress));
        this.mockConfiguration.expects(once()).method("setServerPort")
                .with(eq(newServerPort));

        this.presenter.okButtonActionPerformed();
        assertEquals("Return status comparison",
                ConfigurationPresenter.RETURN_OK,
                this.presenter.getReturnStatus());
    }

    /**
     * Should return status "cancel".
     */
    public void testCancelButtonActionPerformed() {
        this.mockView.expects(once()).method("close");
        this.presenter.cancelButtonActionPerformed();
        assertEquals("Return status comparison",
                ConfigurationPresenter.RETURN_CANCEL,
                this.presenter.getReturnStatus());
    }
}
