package suncertify;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;

@RunWith(TestClassRunner.class)
public class ConfigurationTest extends MockObjectTestCase {

    private Configuration configuration;
    private Mock mockProperties;
    private final String dummyServerPort = "dummy-server-port";
    private final String dummyDatabaseFilePath = "dummy-database-file-path";
    private final String dummyServerAddress = "dummy-server-address";

    @Before
    public void setUp() {
        this.mockProperties = mock(Properties.class);
        this.configuration = new Configuration(
                (Properties) this.mockProperties.proxy());
    }

    @After
    public void verify() {
        super.verify();
    }
    
    @Test(expected=NullPointerException.class)
    public void constructorWithNullProperties() {
        new Configuration(null);
    }

    @Test(expected=NullPointerException.class)
    public void loadConfigurationNullStream() throws Exception {
            this.mockProperties.expects(never()).method("load");
            this.configuration.loadConfiguration(null);
    }

    @Test
    public void loadConfiguration() throws Exception {
        Mock mockInputStream = mock(InputStream.class);
        this.mockProperties.expects(once()).method("load")
                .with(eq(mockInputStream.proxy())).isVoid();
        this.configuration.loadConfiguration(
                (InputStream) mockInputStream.proxy());
    }

    @Test(expected=IOException.class)
    public void loadConfigurationReadError() throws Exception {
        Mock mockInputStream = mock(InputStream.class);
        this.mockProperties.expects(once()).method("load")
                .with(eq(mockInputStream.proxy()))
                .will(throwException(
                        new IOException("Error loading properties")));
        this.configuration.loadConfiguration(
                (InputStream) mockInputStream.proxy());
    }

    @Test(expected=NullPointerException.class)
    public void saveConfigurationNullStream() throws Exception {
        this.mockProperties.expects(never()).method("save");
        this.configuration.saveConfiguration(null);
    }

    @Test
    public void saveConfiguration() throws Exception {
        Mock mockOutputStream = mock(OutputStream.class);
        this.mockProperties.expects(once()).method("store")
                .with(eq(mockOutputStream.proxy()), isA(String.class)).isVoid();
        this.configuration.saveConfiguration(
                (OutputStream) mockOutputStream.proxy());
    }

    @Test(expected=IOException.class)
    public void saveConfigurationWriteError() throws Exception {
        Mock mockOutputStream = mock(OutputStream.class);
        this.mockProperties.expects(once()).method("store")
                .with(eq(mockOutputStream.proxy()), isA(String.class))
                .will(throwException(
                        new IOException("Error storing properties")));
        this.configuration.saveConfiguration(
                (OutputStream) mockOutputStream.proxy());
    }

    @Test
    public void getDatabaseFilePath() {
        this.mockProperties.expects(once()).method("getProperty")
                .with(eq(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY))
                .will(returnValue(this.dummyDatabaseFilePath));
        Assert.assertEquals(this.dummyDatabaseFilePath,
                this.configuration.getDatabaseFilePath());
    }

    @Test
    public void setDatabaseFilePath() {
        this.mockProperties.expects(once()).method("setProperty")
                .with(eq(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY),
                        eq(this.dummyDatabaseFilePath))
                .isVoid();
        this.configuration.setDatabaseFilePath(this.dummyDatabaseFilePath);
    }

    @Test(expected=NullPointerException.class)
    public void setDatabaseFilePathWithNull() {
        this.mockProperties.expects(never()).method("setProperty");
        this.configuration.setDatabaseFilePath(null);
    }

    @Test
    public void getServerAddress() {
        this.mockProperties.expects(once()).method("getProperty")
                .with(eq(ApplicationConstants.SERVER_ADDRESS_PROPERTY))
                .will(returnValue(this.dummyServerAddress));
        Assert.assertEquals(this.dummyServerAddress,
                this.configuration.getServerAddress());
    }

    @Test
    public void setServerAddress() {
        this.mockProperties.expects(once()).method("setProperty")
                .with(eq(ApplicationConstants.SERVER_ADDRESS_PROPERTY),
                        eq(this.dummyServerAddress))
                .isVoid();
        this.configuration.setServerAddress(this.dummyServerAddress);
    }

    @Test(expected=NullPointerException.class)
    public void setServerAddressWithNull() {
        this.mockProperties.expects(never()).method("setProperty");
        this.configuration.setServerAddress(null);
    }

    @Test
    public void getServerPort() {
        this.mockProperties.expects(once()).method("getProperty")
                .with(eq(ApplicationConstants.SERVER_PORT_PROPERTY))
                .will(returnValue(this.dummyServerPort));
        Assert.assertEquals(this.dummyServerPort, this.configuration.getServerPort());
    }

    @Test
    public void setServerPort() {
        this.mockProperties.expects(once()).method("setProperty")
                .with(eq(ApplicationConstants.SERVER_PORT_PROPERTY),
                        eq(this.dummyServerPort))
                .isVoid();
        this.configuration.setServerPort(this.dummyServerPort);
    }

    @Test(expected=NullPointerException.class)
    public void setServerPortWithNull() {
        this.mockProperties.expects(never()).method("setProperty");
        this.configuration.setServerPort(null);
    }
}
