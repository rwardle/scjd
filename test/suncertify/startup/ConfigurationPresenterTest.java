/*
 * ConfigurationPresenterTest.java
 *
 * Created on 10 June 2005
 */


package suncertify.startup;

import java.awt.event.ActionListener;
import java.util.logging.Logger;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.constraint.IsInstanceOf;


/**
 * Unit tests for {@link ConfigurationPresenterTest}.
 *
 * @author Richard Wardle
 */
public final class ConfigurationPresenterTest extends MockObjectTestCase {

    private static Logger logger = Logger
            .getLogger(ConfigurationPresenterTest.class.getName());

    private String databaseFilePath;
    private String serverAddress;
    private String serverPort;
    private Configuration configuration;
    private Mock mockView;

    /**
     * Creates a new instance of ConfigurationPresenterTest.
     *
     * @param name Test case name.
     */
    public ConfigurationPresenterTest(String name) {
        super(name);
        this.databaseFilePath = "databaseFilePath";
        this.serverAddress = "serverAddress";
        this.serverPort = "serverPort";
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() {
        this.configuration = new Configuration("dummy.properties");
        this.configuration.setDatabaseFilePath(this.databaseFilePath);
        this.configuration.setServerAddress(this.serverAddress);
        this.configuration.setServerPort(this.serverPort);
        this.mockView = new Mock(ConfigurationView.class);
    }

    private void setUpMockView() {
        this.mockView.expects(once()).method("addOkButtonListener")
                .with(new IsInstanceOf(ActionListener.class));
        this.mockView.expects(once()).method("addCancelButtonListener")
                .with(new IsInstanceOf(ActionListener.class));
        this.mockView.expects(once()).method("setDatabaseFilePath")
                .with(eq(this.databaseFilePath));
        this.mockView.expects(once()).method("setServerAddress")
                .with(eq(this.serverAddress));
        this.mockView.expects(once()).method("setServerPort")
                .with(eq(this.serverPort));
    }

    /**
     * Tests {@link ConfigurationPresenter#ConfigurationPresenter(
     * Configuration,ConfigurationView)} with a null configuration.
     */
    public void testConstructionNullConfiguration() {
        try {
            new ConfigurationPresenter(null,
                    (ConfigurationView) this.mockView.proxy());
            fail("NullPointerException expected when constructor called with "
                    + "null configuration");
        } catch (NullPointerException e) {
            ConfigurationPresenterTest.logger
                    .info("Caught expected NullPointerException: "
                            + e.getMessage());
        }
    }

    /**
     * Tests {@link ConfigurationPresenter#ConfigurationPresenter(
     * Configuration,ConfigurationView)} with a null view.
     */
    public void testConstructionNullView() {
        try {
            new ConfigurationPresenter(this.configuration, null);
            fail("NullPointerException expected when constructor called with "
                    + "null view");
        } catch (NullPointerException e) {
            ConfigurationPresenterTest.logger
                    .info("Caught expected NullPointerException: "
                            + e.getMessage());
        }
    }

    /**
     * Tests {@link ConfigurationPresenter#ConfigurationPresenter(
     * Configuration,ConfigurationView)}.
     */
    public void testConstruction() {
        setUpMockView();
        ConfigurationPresenter presenter = new ConfigurationPresenter(
                this.configuration,
                (ConfigurationView) this.mockView.proxy());
        assertEquals(
                "Return status should be '"
                        + ConfigurationPresenter.RETURN_CANCEL
                        + "'after construction",
                ConfigurationPresenter.RETURN_CANCEL,
                presenter.getReturnStatus());
    }

    /**
     * Tests {@link ConfigurationPresenter#okButtonActionPerformed}.
     */
    public void testOkButtonActionPerformed() {
        setUpMockView();
        String newDatabaseFilePath = "newDatabaseFilePath";
        this.mockView.expects(once()).method("getDatabaseFilePath")
                .will(returnValue(newDatabaseFilePath));
        String newServerAddress = "newServerAddress";
        this.mockView.expects(once()).method("getServerAddress")
                .will(returnValue(newServerAddress));
        String newServerPort = "newServerPort";
        this.mockView.expects(once()).method("getServerPort")
                .will(returnValue(newServerPort));
        this.mockView.expects(once()).method("close");

        ConfigurationPresenter presenter = new ConfigurationPresenter(
                this.configuration,
                (ConfigurationView) this.mockView.proxy());

        presenter.okButtonActionPerformed();
        assertEquals("Return status comparison",
                ConfigurationPresenter.RETURN_OK, presenter.getReturnStatus());
        assertEquals("Model database file path comparison,",
                this.configuration.getDatabaseFilePath(), newDatabaseFilePath);
        assertEquals("Model server address comparison,",
                this.configuration.getServerAddress(), newServerAddress);
        assertEquals("Model server port comparison,",
                this.configuration.getServerPort(), newServerPort);
    }

    /**
     * Tests {@link ConfigurationPresenter#cancelButtonActionPerformed}.
     */
    public void testCancelButtonActionPerformed() {
        setUpMockView();
        this.mockView.expects(once()).method("close");

        ConfigurationPresenter presenter = new ConfigurationPresenter(
                this.configuration,
                (ConfigurationView) this.mockView.proxy());

        presenter.cancelButtonActionPerformed();
        assertEquals("Return status comparison",
                ConfigurationPresenter.RETURN_CANCEL,
                presenter.getReturnStatus());
        assertEquals("Model database file path comparison,",
                this.configuration.getDatabaseFilePath(),
                this.databaseFilePath);
        assertEquals("Model server address comparison,",
                this.configuration.getServerAddress(), this.serverAddress);
        assertEquals("Model server port comparison,",
                this.configuration.getServerPort(), this.serverPort);
    }
}
