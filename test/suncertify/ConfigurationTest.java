/*
 * ConfigurationTest.java
 *
 * Created on 05-Jun-2005
 */


package suncertify;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;


/**
 * Unit tests for {@link suncertify.Configuration}.
 *
 * @author Richard Wardle
 */
public final class ConfigurationTest extends MockObjectTestCase {

    private static Logger logger = Logger.getLogger(ConfigurationTest.class
            .getName());

    private Configuration configuration;
    private Mock mockProperties;
    private String dummyServerPort;
    private String dummyDatabaseFilePath;
    private String dummyServerAddress;

    /**
     * Creates a new instance of <code>ConfigurationTest</code>.
     */
    public ConfigurationTest() {
        super();
        this.dummyServerPort = "dummy-server-port";
        this.dummyDatabaseFilePath = "dummy-database-file-path";
        this.dummyServerAddress = "dummy-server-address";
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.mockProperties = mock(Properties.class);
        this.configuration = new Configuration(
                (Properties) this.mockProperties.proxy());
    }

    /**
     * Tests {@link Configuration#Configuration(Properties)} with a
     * <code>null</code> properties object.
     */
    public void testConstructorWithNullProperties() {
        try {
            new Configuration(null);
            fail("NullPointerException expected when properties object is "
                    + "null");
        } catch (NullPointerException e) {
            ConfigurationTest.logger.info("Caught expected "
                    + "NullPointerException: " + e.getMessage());
        }
    }

    /**
     * Should throw <code>NullPointerException</code> if the input stream is
     * <code>null</code>.
     *
     * @throws IOException If there is an IO error.
     */
    public void testLoadConfigurationNullStream() throws IOException {
        try {
            this.mockProperties.expects(never()).method("load");
            this.configuration.loadConfiguration(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            ConfigurationTest.logger.info(
                    "Caught expected NullPointerException: " + e.getMessage());
        }
    }

    /**
     * Should call the <code>load</code> method on the properties object.
     *
     * @throws IOException If there is an IO error.
     */
    public void testLoadConfiguration() throws IOException {
        Mock mockInputStream = mock(InputStream.class);
        this.mockProperties.expects(once()).method("load")
                .with(eq(mockInputStream.proxy())).isVoid();
        this.configuration.loadConfiguration(
                (InputStream) mockInputStream.proxy());
    }

    /**
     * Should throw <code>IOException</code> if the properties cannot be read.
     */
    public void testLoadConfigurationReadError() {
        try {
            Mock mockInputStream = mock(InputStream.class);
            this.mockProperties.expects(once()).method("load")
                    .with(eq(mockInputStream.proxy()))
                    .will(throwException(
                            new IOException("Error loading properties")));
            this.configuration.loadConfiguration(
                    (InputStream) mockInputStream.proxy());
            fail("IOException expected");
        } catch (IOException e) {
            ConfigurationTest.logger.info("Caught expected IOException: "
                    + e.getMessage());
        }
    }

    /**
     * Should throw <code>NullPointerException</code> if the output stream is
     * <code>null</code>.
     *
     * @throws IOException If there is an IO error.
     */
    public void testSaveConfigurationNullStream() throws IOException {
        try {
            this.mockProperties.expects(never()).method("save");
            this.configuration.saveConfiguration(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            ConfigurationTest.logger.info(
                    "Caught expected NullPointerException: " + e.getMessage());
        }
    }

    /**
     * Should call the <code>store</code> method on the properties object.
     *
     * @throws IOException If there is an IO error.
     */
    public void testSaveConfiguration() throws IOException {
        Mock mockOutputStream = mock(OutputStream.class);
        this.mockProperties.expects(once()).method("store")
                .with(eq(mockOutputStream.proxy()), isA(String.class)).isVoid();
        this.configuration.saveConfiguration(
                (OutputStream) mockOutputStream.proxy());
    }

    /**
     * Should throw an <code>IOException</code> if the properties file cannot
     * be written.
     */
    public void testSaveConfigurationWriteError() {
        try {
            Mock mockOutputStream = mock(OutputStream.class);
            this.mockProperties.expects(once()).method("store")
                    .with(eq(mockOutputStream.proxy()), isA(String.class))
                    .will(throwException(
                            new IOException("Error storing properties")));
            this.configuration.saveConfiguration(
                    (OutputStream) mockOutputStream.proxy());
            fail("IOException expected");
        } catch (IOException e) {
            ConfigurationTest.logger.info("Caught expected IOException: "
                    + e.getMessage());
        }
    }

    /**
     * Should call the <code>getProperty</code> method on the properties object
     * with the database file path property name.
     */
    public void testGetDatabaseFilePath() {
        this.mockProperties.expects(once()).method("getProperty")
                .with(eq(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY))
                .will(returnValue(this.dummyDatabaseFilePath));
        assertEquals(this.dummyDatabaseFilePath,
                this.configuration.getDatabaseFilePath());
    }

    /**
     * Should call the <code>setProperty</code> method on the properties object
     * with the database file path property name and new value.
     */
    public void testSetDatabaseFilePath() {
        this.mockProperties.expects(once()).method("setProperty")
                .with(eq(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY),
                        eq(this.dummyDatabaseFilePath))
                .isVoid();
        this.configuration.setDatabaseFilePath(this.dummyDatabaseFilePath);
    }

    /**
     * Should throw <code>NullPointerException</code> when argument is
     * <code>null</code>.
     */
    public void testSetDatabaseFilePathWithNull() {
        try {
            this.mockProperties.expects(never()).method("setProperty");
            this.configuration.setDatabaseFilePath(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            ConfigurationTest.logger.info(
                    "Caught expected NullPointerException: " + e.getMessage());
        }
    }

    /**
     * Should call the <code>getProperty</code> method on the properties object
     * with the server address property name.
     */
    public void testGetServerAddress() {
        this.mockProperties.expects(once()).method("getProperty")
                .with(eq(ApplicationConstants.SERVER_ADDRESS_PROPERTY))
                .will(returnValue(this.dummyServerAddress));
        assertEquals(this.dummyServerAddress,
                this.configuration.getServerAddress());
    }

    /**
     * Should call the <code>setProperty</code> method on the properties object
     * with the server address property name and new value.
     */
    public void testSetServerAddress() {
        this.mockProperties.expects(once()).method("setProperty")
                .with(eq(ApplicationConstants.SERVER_ADDRESS_PROPERTY),
                        eq(this.dummyServerAddress))
                .isVoid();
        this.configuration.setServerAddress(this.dummyServerAddress);
    }

    /**
     * Should throw <code>NullPointerException</code> when argument is
     * <code>null</code>.
     */
    public void testSetServerAddressWithNull() {
        try {
            this.mockProperties.expects(never()).method("setProperty");
            this.configuration.setServerAddress(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            ConfigurationTest.logger.info(
                    "Caught expected NullPointerException: " + e.getMessage());
        }
    }

    /**
     * Should call the <code>getProperty</code> method on the properties object
     * with the server port property name.
     */
    public void testGetServerPort() {
        this.mockProperties.expects(once()).method("getProperty")
                .with(eq(ApplicationConstants.SERVER_PORT_PROPERTY))
                .will(returnValue(this.dummyServerPort));
        assertEquals(this.dummyServerPort, this.configuration.getServerPort());
    }

    /**
     * Should call the <code>setProperty</code> method on the properties object
     * with the server port property name and new value.
     */
    public void testSetServerPort() {
        this.mockProperties.expects(once()).method("setProperty")
                .with(eq(ApplicationConstants.SERVER_PORT_PROPERTY),
                        eq(this.dummyServerPort))
                .isVoid();
        this.configuration.setServerPort(this.dummyServerPort);
    }

    /**
     * Should throw <code>NullPointerException</code> when argument is
     * <code>null</code>.
     */
    public void testSetServerPortWithNull() {
        try {
            this.mockProperties.expects(never()).method("setProperty");
            this.configuration.setServerPort(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            ConfigurationTest.logger.info(
                    "Caught expected NullPointerException: " + e.getMessage());
        }
    }
}
