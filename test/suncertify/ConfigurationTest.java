package suncertify;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;

@RunWith(TestClassRunner.class)
public class ConfigurationTest {

    private final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private final String dummyServerPort = "dummy-server-port";
    private final String dummyDatabaseFilePath = "dummy-database-file-path";
    private final String dummyServerAddress = "dummy-server-address";
    private Properties mockProperties;
    private Configuration configuration;

    @Before
    public void setUp() {
        this.mockProperties = this.context.mock(Properties.class);
        this.configuration = new Configuration(this.mockProperties);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = NullPointerException.class)
    public void cannotBeConstructedWithNullProperties() {
        new Configuration(null);
    }

    @Test(expected = NullPointerException.class)
    public void cannotLoadFromNullInput() throws Exception {
        this.context.checking(new Expectations() {{
            never(ConfigurationTest.this.mockProperties).load(
                    with(any(InputStream.class)));
        }});
        this.configuration.loadConfiguration(null);
    }

    @Test
    public void loadConfigurationLoadsProperties() throws Exception {
        final InputStream mockInputStream = this.context
                .mock(InputStream.class);
        this.context.checking(new Expectations() {{
            one(ConfigurationTest.this.mockProperties).load(
                    with(same(mockInputStream)));
        }});
        this.configuration.loadConfiguration(mockInputStream);
    }

    @Test(expected = IOException.class)
    public void loadConfigurationThrowsExceptionWhenPropertiesCannotBeRead()
            throws Exception {
        final InputStream mockInputStream = this.context
                .mock(InputStream.class);
        this.context.checking(new Expectations() {{
            one(ConfigurationTest.this.mockProperties).load(
                    with(same(mockInputStream)));
               will(throwException(new IOException()));
        }});
        this.configuration.loadConfiguration(mockInputStream);
    }

    @Test(expected = NullPointerException.class)
    public void cannotSaveToNullOutput() throws Exception {
        this.context.checking(new Expectations() {{
            never(ConfigurationTest.this.mockProperties).store(
                    with(any(OutputStream.class)), with(an(String.class)));
        }});
        this.configuration.saveConfiguration(null);
    }

    @Test
    public void saveConfigurationSavesProperties() throws Exception {
        final OutputStream mockOutputStream = this.context
                .mock(OutputStream.class);
        this.context.checking(new Expectations() {{
            one(ConfigurationTest.this.mockProperties).store(
                    with(same(mockOutputStream)), with(an(String.class)));
        }});
        this.configuration.saveConfiguration(mockOutputStream);
    }

    @Test(expected = IOException.class)
    public void saveConfigurationThrowsExceptionWhenPropertiesCannotBeWritten()
            throws Exception {
        final OutputStream mockOutputStream = this.context
                .mock(OutputStream.class);
        this.context.checking(new Expectations() {{
            one(ConfigurationTest.this.mockProperties).store(
                    with(same(mockOutputStream)), with(an(String.class)));
               will(throwException(new IOException()));
        }});
        this.configuration.saveConfiguration(mockOutputStream);
    }

    @Test
    public void getDatabaseFilePath() {
        this.context.checking(new Expectations() {{
            one(ConfigurationTest.this.mockProperties)
                    .getProperty(
                            with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)));
               will(returnValue(ConfigurationTest.this.dummyDatabaseFilePath));
        }});
        Assert.assertEquals(this.dummyDatabaseFilePath, this.configuration
                .getDatabaseFilePath());
    }

    @Test
    public void setDatabaseFilePath() {
        this.context.checking(new Expectations() {{
            one(ConfigurationTest.this.mockProperties)
                    .setProperty(
                            with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)),
                            with(equal(ConfigurationTest.this.dummyDatabaseFilePath)));
        }});
        this.configuration.setDatabaseFilePath(this.dummyDatabaseFilePath);
    }

    @Test(expected = NullPointerException.class)
    public void cannotSetDatabaseFilePathToNull() {
        this.context.checking(new Expectations() {{
            never(ConfigurationTest.this.mockProperties).setProperty(
                    with(any(String.class)), with(any(String.class)));
        }});
        this.configuration.setDatabaseFilePath(null);
    }

    @Test
    public void getServerAddress() {
        this.context.checking(new Expectations() {{
            one(ConfigurationTest.this.mockProperties)
                    .getProperty(
                            with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)));
               will(returnValue(ConfigurationTest.this.dummyServerAddress));
        }});
        Assert.assertEquals(this.dummyServerAddress, this.configuration
                .getServerAddress());
    }

    @Test
    public void setServerAddress() {
        this.context.checking(new Expectations() {{
            one(ConfigurationTest.this.mockProperties)
                    .setProperty(
                            with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)),
                            with(equal(ConfigurationTest.this.dummyServerAddress)));
        }});
        this.configuration.setServerAddress(this.dummyServerAddress);
    }

    @Test(expected = NullPointerException.class)
    public void cannotSetServerAddressToNull() {
        this.context.checking(new Expectations() {{
            never(ConfigurationTest.this.mockProperties).setProperty(
                    with(any(String.class)), with(any(String.class)));
        }});
        this.configuration.setServerAddress(null);
    }

    @Test
    public void getServerPort() {
        this.context.checking(new Expectations() {{
            one(ConfigurationTest.this.mockProperties).getProperty(
                    with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)));
               will(returnValue(ConfigurationTest.this.dummyServerPort));
        }});
        Assert.assertEquals(this.dummyServerPort, this.configuration
                .getServerPort());
    }

    @Test
    public void setServerPort() {
        this.context.checking(new Expectations() {{
            one(ConfigurationTest.this.mockProperties).setProperty(
                    with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)),
                    with(equal(ConfigurationTest.this.dummyServerPort)));
        }});
        this.configuration.setServerPort(this.dummyServerPort);
    }

    @Test(expected = NullPointerException.class)
    public void cannotSetServerPortToNull() {
        this.context.checking(new Expectations() {{
            never(ConfigurationTest.this.mockProperties).setProperty(
                    with(any(String.class)), with(any(String.class)));
        }});
        this.configuration.setServerPort(null);
    }
}
