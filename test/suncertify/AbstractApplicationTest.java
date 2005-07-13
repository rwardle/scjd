/*
 * AbstractApplicationTest.java
 *
 * Created on 07-Jul-2005
 */


package suncertify;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.expectation.AssertMo;

import suncertify.presentation.ConfigurationPresenter;
import suncertify.presentation.ConfigurationView;


/**
 * Unit tests for {@link AbstractApplicationTest}.
 *
 * @author Richard Wardle
 */
public final class AbstractApplicationTest extends MockObjectTestCase {

    private static Logger logger = Logger.getLogger(
            AbstractApplicationTest.class.getName());

    Mock mockPresenter;
    Mock mockView;
    private AbstractApplication application;
    private String dummyPropertiesFilePath;
    private Mock mockConfiguration;

    /**
     * Creates a new instance of <code>AbstractApplicationTest</code>.
     */
    public AbstractApplicationTest() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() {
        this.dummyPropertiesFilePath = "dummy-properties-file-path";
        this.mockConfiguration = mock(Configuration.class,
                new Class[] {Properties.class},
                new Object[] {new Properties()});
        this.application = new StubAbstractApplication(
                (Configuration) this.mockConfiguration.proxy());
        this.mockView = mock(ConfigurationView.class);
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() {
        File dummyPropertiesFile = new File(this.dummyPropertiesFilePath);
        if (dummyPropertiesFile.exists()) {
            dummyPropertiesFile.delete();
        }
    }

    private void setUpDefaultExpectations() {
        this.mockPresenter = mock(ConfigurationPresenter.class,
                new Class[] {Configuration.class, ConfigurationView.class},
                new Object[] {
                    (Configuration) this.mockConfiguration.proxy(),
                    (ConfigurationView) this.mockView.proxy(),
                });
        this.mockPresenter.stubs().method(eq("initialiseView"));
        this.mockPresenter.stubs().method(eq("realiseView"));
        this.mockPresenter.stubs().method(eq("getReturnStatus"))
                .will(returnValue(ConfigurationPresenter.RETURN_CANCEL));
        this.mockConfiguration.stubs().method("loadConfiguration");
        this.mockConfiguration.stubs().method("saveConfiguration");
    }

    /**
     * Should throw a <code>NullPointerException</code> if the configuration is
     * <code>null</code>.
     */
    public void testConstructorDisallowNullConfiguration() {
        try {
            new StubAbstractApplication(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            AbstractApplicationTest.logger.info(
                    "Caught expected NullPointerException: " + e.getMessage());
        }
    }

    /**
     * Should throw a <code>NullPointerException</code> if the properties file
     * is <code>null</code>.
     *
     * @throws ApplicationException If there is a configuration error.
     */
    public void testConfigureDisallowNullPropertiesFile() throws
            ApplicationException {
        try {
            this.application.configure(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            AbstractApplicationTest.logger.info(
                    "Caught expected NullPointerException: " + e.getMessage());
        }
    }

    /**
     * Should not load the configuration when the properties file does not
     * exist.
     *
     * @throws ApplicationException If there is a configuration error.
     */
    public void testConfigureNotLoadConfigurationWhenPropertiesFileNotExist()
            throws ApplicationException {
        setUpDefaultExpectations();
        this.mockConfiguration.expects(never()).method("loadConfiguration");
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    /**
     * Should load the configuration when the properties file exists.
     *
     * @throws IOException If the dummy file cannot be created.
     * @throws ApplicationException If there is a configuration error.
     */
    public void testConfigureLoadConfigurationWhenPropertiesFileExists() throws
            IOException, ApplicationException {
        setUpDefaultExpectations();
        File propertiesFile = new File(this.dummyPropertiesFilePath);
        propertiesFile.createNewFile();
        this.mockConfiguration.expects(once()).method("loadConfiguration")
                .with(isA(InputStream.class));
        this.application.configure(propertiesFile);
    }

    /**
     * Should return <code>false</code> if the user cancels the configuration
     * process.
     *
     * @throws ApplicationException If there is a configuration error.
     */
    public void testConfigureReturnFalseWhenCancelled() throws
            ApplicationException {
        setUpDefaultExpectations();
        this.mockPresenter.expects(once()).method("getReturnStatus")
                .will(returnValue(ConfigurationPresenter.RETURN_CANCEL));
        assertFalse("User should cancel configuration process",
                this.application.configure(
                        new File(this.dummyPropertiesFilePath)));
    }

    /**
     * Should not save the configuration if the user cancels the configuration
     * process.
     *
     * @throws ApplicationException If there is a configuration error.
     */
    public void testConfigureNotSaveConfigurationWhenCancelled() throws
            ApplicationException {
        setUpDefaultExpectations();
        this.mockConfiguration.expects(never()).method("saveConfiguration");
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    /**
     * Should return <code>true</code> if the user okays the configuration
     * process.
     *
     * @throws ApplicationException If there is a configuration error.
     */
    public void testConfigureReturnTrueWhenOkayed() throws
            ApplicationException {
        setUpDefaultExpectations();
        this.mockPresenter.expects(once()).method(eq("getReturnStatus"))
                .will(returnValue(ConfigurationPresenter.RETURN_OK));
        assertTrue("User should OK configuration process",
                this.application.configure(
                        new File(this.dummyPropertiesFilePath)));
    }

    /**
     * Should save the configuration if the user okays the configuration
     * process.
     *
     * @throws ApplicationException If there is a configuration error.
     */
    public void testConfigureSaveConfigurationWhenOkayed() throws
            ApplicationException {
        setUpDefaultExpectations();
        this.mockPresenter.expects(once()).method(eq("getReturnStatus"))
                .will(returnValue(ConfigurationPresenter.RETURN_OK));
        this.mockConfiguration.expects(once()).method("saveConfiguration")
                .with(isA(OutputStream.class));
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    /**
     * Should show the configuration view.
     *
     * @throws ApplicationException If there is a configuration error.
     */
    public void testConfigureDisplaysView() throws ApplicationException {
        setUpDefaultExpectations();
        this.mockPresenter.expects(once()).method(eq("initialiseView"));
        this.mockPresenter.expects(once()).method(eq("realiseView"));
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    /**
     * Should throw an <code>ApplicationException</code> when the
     * <code>loadConfiguration</code> method throws an <code>IOException</code>.
     *
     * @throws IOException If the dummy properties file cannot be created.
     */
    public void testConfigureWhenLoadConfigurationFails() throws IOException {
        setUpDefaultExpectations();
        File propertiesFile = new File(this.dummyPropertiesFilePath);
        propertiesFile.createNewFile();
        this.mockConfiguration.expects(once()).method("loadConfiguration")
                .with(isA(InputStream.class))
                .will(throwException(new IOException()));
        this.mockPresenter.expects(never()).method(eq("getReturnStatus"));
        this.mockConfiguration.expects(never()).method("saveConfiguration");

        try {
            this.application.configure(propertiesFile);
            fail("ApplicationException expected");
        } catch (ApplicationException e) {
            AbstractApplicationTest.logger.info(
                    "Caught expected ApplicationException: " + e.getMessage());
        }
    }

    /**
     * Should throw an <code>ApplicationException</code> when the
     * <code>saveConfiguration</code> method throws an <code>IOException</code>.
     */
    public void testConfigureWhenSaveConfigurationFails() {
        setUpDefaultExpectations();
        this.mockPresenter.expects(once()).method(eq("getReturnStatus"))
                .will(returnValue(ConfigurationPresenter.RETURN_OK));
        this.mockConfiguration.expects(once()).method("saveConfiguration")
                .with(isA(OutputStream.class))
                .will(throwException(new IOException()));

        try {
            this.application.configure(new File(this.dummyPropertiesFilePath));
            fail("ApplicationException expected");
        } catch (ApplicationException e) {
            AbstractApplicationTest.logger.info(
                    "Caught expected ApplicationException: " + e.getMessage());
        }
    }

    private class StubAbstractApplication extends AbstractApplication {

        StubAbstractApplication(Configuration configuration) {
            super(configuration);
        }

        /**
         * {@inheritDoc}
         */
        protected ConfigurationPresenter createConfigurationPresenter() {
            return (ConfigurationPresenter) AbstractApplicationTest.this
                    .mockPresenter.proxy();
        }

        /**
         * {@inheritDoc}
         */
        protected ConfigurationView createConfigurationView() {
            return (ConfigurationView) AbstractApplicationTest.this
                    .mockView.proxy();
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            AssertMo.notImplemented("StubAbstractApplication");
        }
    }
}
