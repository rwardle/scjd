/*
 * MainTest.java
 *
 * Created on 05-Jul-2005
 */


package suncertify;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;


/**
 * Unit tests for {@link suncertify.Main}.
 *
 * @author Richard Wardle
 */
public final class MainTest extends MockObjectTestCase {

    private static Logger logger = Logger.getLogger(MainTest.class.getName());

    private Main main;
    private Mock mockConfiguration;
    private Mock mockApplication;
    private String dummyPropertiesFilePath;

    /**
     * Creates a new instance of <code>MainTest</code>.
     */
    public MainTest() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() {
        this.dummyPropertiesFilePath = "dummy-properties-file-path";
        this.main = new Main();
        this.mockConfiguration = mock(Configuration.class,
                new Class[] {Properties.class},
                new Object[] {new Properties()});
        this.mockApplication = mock(Application.class);
    }

    /**
     * Should throw a <code>NullPointerException</code> when the command-line
     * arguments are <code>null</code>.
     */
    public void testGetApplicationModeDisallowNullArgs() {
        try {
            this.main.getApplicationMode(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            MainTest.logger.info("Caught expected NullPointerException: "
                    + e.getMessage());
        }
    }

    /**
     * Should throw a <code>IllegalArgumentException</code> when the
     * command-line argument is invalid.
     */
    public void testGetApplicationModeDisallowInvalidArg() {
        try {
            this.main.getApplicationMode(new String[] {"invalid-mode"});
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            MainTest.logger.info("Caught expected "
                    + "IllegalArgumentException: " + e.getMessage());
        }
    }

    /**
     * Should give client application mode when there are no command-line
     * arguments.
     */
    public void testGetApplicationModeWithNoArgs() {
        assertEquals("Application mode comparison,", ApplicationMode.CLIENT,
                this.main.getApplicationMode(new String[0]));
    }

    /**
     * Should give server application mode when the command-line argument is
     * "server".
     */
    public void testGetApplicationModeWithServerArg() {
        assertEquals("Application mode comparison,", ApplicationMode.SERVER,
                this.main.getApplicationMode(new String[] {"server"}));
    }

    /**
     * Should give standalone mode when the command-line argument is "alone".
     */
    public void testGetApplicationModeWithAloneArg() {
        assertEquals("Application mode comparison,", ApplicationMode.STANDALONE,
                this.main.getApplicationMode(new String[] {"alone"}));
    }

    /**
     * Should create client application when called with client application
     * mode.
     */
    public void testCreateApplicationWithClientMode() {
        assertTrue("Instance of ClientApplication expected",
                this.main.createApplication(ApplicationMode.CLIENT,
                        (Configuration) this.mockConfiguration.proxy())
                instanceof ClientApplication);
    }

    /**
     * Should create server application when called with server application
     * mode.
     */
    public void testCreateApplicationWithServerMode() {
        assertTrue("Instance of ServerApplication expected",
                this.main.createApplication(ApplicationMode.SERVER,
                        (Configuration) this.mockConfiguration.proxy())
                instanceof ServerApplication);
    }

    /**
     * Should create standalone application when called with standalone
     * application mode.
     */
    public void testCreateApplicationWithStandaloneMode() {
        assertTrue("Instance of StandaloneApplication expected",
                this.main.createApplication(ApplicationMode.STANDALONE,
                        (Configuration) this.mockConfiguration.proxy())
                instanceof StandaloneApplication);
    }

    /**
     * Should throw a <code>NullPointerException</code> when called with a
     * <code>null</code> application argument.
     */
    public void testConfigureApplicationDisallowNullApplication() {
        try {
            this.main.configureApplication(null, this.dummyPropertiesFilePath);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            MainTest.logger.info("Caught expected NullPointerException: "
                    + e.getMessage());
        }
    }

    /**
     * Should throw a <code>NullPointerException</code> when called with a
     * <code>null</code> properties file path argument.
     */
    public void testConfigureApplicationDisallowNullPropertiesFilePath() {
        try {
            this.main.configureApplication(
                    (Application) this.mockApplication.proxy(), null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            MainTest.logger.info("Caught expected NullPointerException: "
                    + e.getMessage());
        }
    }

    /**
     * Should should show the error dialog and exit the application when the
     * configure method throws an <code>ApplicationException</code>.
     */
    public void testConfigureApplicationWhenConfigureThrowsException() {
        this.mockApplication.expects(once()).method("configure")
                .with(isA(File.class)).will(throwException(
                        new ApplicationException()));
        this.mockApplication.expects(once()).method("showErrorDialog")
                .with(isA(String.class));
        this.mockApplication.expects(once()).method("exit").with(eq(1));
        this.main.configureApplication(
                (Application) this.mockApplication.proxy(),
                this.dummyPropertiesFilePath);
    }

    /**
     * Should not exit the application when the configuration is okayed by the
     * user.
     */
    public void testConfigureApplicationNotExitWhenOkayed() {
        this.mockApplication.expects(once()).method("configure")
                .with(isA(File.class))
                .will(returnValue(true));
        this.mockApplication.expects(never()).method("exit");
        this.main.configureApplication(
                (Application) this.mockApplication.proxy(),
                this.dummyPropertiesFilePath);
    }

    /**
     * Should exit the application when the configuration is cancelled by the
     * user.
     */
    public void testConfigureApplicationExitWhenCancelled() {
        this.mockApplication.expects(once()).method("configure")
                .will(returnValue(false));
        this.mockApplication.expects(once()).method("exit").with(eq(0));
        this.main.configureApplication(
                (Application) this.mockApplication.proxy(),
                this.dummyPropertiesFilePath);
    }

    /**
     * Should throw a <code>NullPointerException</code> if called with a
     * <code>null</code> application argument.
     */
    public void testRunApplicationDisallowNullApplication() {
        try {
            this.main.runApplication(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            MainTest.logger.info("Caught expected NullPointerException: "
                    + e.getMessage());
        }
    }

    /**
     * Should call the application run method.
     */
    public void testRunApplication() {
        this.mockApplication.expects(once()).method("run");
        this.main.runApplication((Application) this.mockApplication.proxy());
    }

    /**
     * Should show the error dialog and exit the application when the run method
     * throws an <code>ApplicationException</code>.
     */
    public void testRunApplicationRunThrowsApplicationException() {
        this.mockApplication.expects(once()).method("run")
                .will(throwException(new ApplicationException()));
        this.mockApplication.expects(once()).method("showErrorDialog")
                .with(isA(String.class));
        this.mockApplication.expects(once()).method("exit")
                .with(eq(1));
        this.main.runApplication((Application) this.mockApplication.proxy());
    }
}
