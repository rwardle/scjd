package suncertify;

import static suncertify.ApplicationConstants.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationManagerTest {

    private final Mockery context = new Mockery();
    private Configuration mockConfiguration;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfConfigurationIsNull() {
        new ConfigurationManager(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfDatabaseFilePathIsNull() throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(ConfigurationManagerTest.this.mockConfiguration);
        }});
        ConfigurationManager configurationManager = new ConfigurationManager(
                this.mockConfiguration);
        configurationManager.setDatabaseFilePath(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfServerAddressIsNull() throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(ConfigurationManagerTest.this.mockConfiguration);
        }});
        ConfigurationManager configurationManager = new ConfigurationManager(
                this.mockConfiguration);
        configurationManager.setServerAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfServerPortIsNull() throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(ConfigurationManagerTest.this.mockConfiguration);
        }});
        ConfigurationManager configurationManager = new ConfigurationManager(
                this.mockConfiguration);
        configurationManager.setServerPort(null);
    }

    @Test
    public void doesNotLoadConfigurationIfItDoesNotExist() throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(ConfigurationManagerTest.this.mockConfiguration)
                    .getProperty(with(any(String.class)));
            allowing(ConfigurationManagerTest.this.mockConfiguration).exists();
                will(returnValue(false));
            never(ConfigurationManagerTest.this.mockConfiguration).load();
        }});
        new ConfigurationManager(this.mockConfiguration);
    }

    @Test
    public void loadsConfigurationIfItExists() throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(ConfigurationManagerTest.this.mockConfiguration)
                    .getProperty(with(any(String.class)));
            allowing(ConfigurationManagerTest.this.mockConfiguration).exists();
                will(returnValue(true));
            one(ConfigurationManagerTest.this.mockConfiguration).load();
        }});
        new ConfigurationManager(this.mockConfiguration);
    }

    @Test
    public void getsDefaultConfigurationIfPropertyIsNull() throws Exception {
        this.context.checking(new Expectations() {{ 
            ignoring(ConfigurationManagerTest.this.mockConfiguration).exists();
            allowing(ConfigurationManagerTest.this.mockConfiguration)
                    .getProperty(with(an(String.class)));
                will(returnValue(null));
            one(ConfigurationManagerTest.this.mockConfiguration).setProperty(
                    with(equal(DATABASE_FILE_PATH_PROPERTY)),
                    with(equal(DEFAULT_DATABASE_FILE_PATH)));
            one(ConfigurationManagerTest.this.mockConfiguration).setProperty(
                    with(equal(SERVER_ADDRESS_PROPERTY)),
                    with(equal(DEFAULT_SERVER_ADDRESS)));
            one(ConfigurationManagerTest.this.mockConfiguration).setProperty(
                    with(equal(SERVER_PORT_PROPERTY)),
                    with(equal(DEFAULT_SERVER_PORT)));
        }});
        new ConfigurationManager(this.mockConfiguration);
    }

    @Test
    public void settersUpdateConfiguration() throws Exception {
        final String newDatabaseFilePath = "newDatabaseFilePath";
        final String newServerAddress = "newServerAddress";
        final String newServerPort = "newServerPort";
        this.context.checking(new Expectations() {{
            ignoring(ConfigurationManagerTest.this.mockConfiguration).exists();
            ignoring(ConfigurationManagerTest.this.mockConfiguration)
                    .getProperty(with(any(String.class)));
            one(ConfigurationManagerTest.this.mockConfiguration).setProperty(
                    with(equal(DATABASE_FILE_PATH_PROPERTY)),
                    with(equal(newDatabaseFilePath)));
            one(ConfigurationManagerTest.this.mockConfiguration).setProperty(
                    with(equal(SERVER_ADDRESS_PROPERTY)),
                    with(equal(newServerAddress)));
            one(ConfigurationManagerTest.this.mockConfiguration).setProperty(
                    with(equal(SERVER_PORT_PROPERTY)),
                    with(equal(newServerPort)));
        }});
        ConfigurationManager configurationManager = new ConfigurationManager(
                this.mockConfiguration);
        configurationManager.setDatabaseFilePath(newDatabaseFilePath);
        configurationManager.setServerAddress(newServerAddress);
        configurationManager.setServerPort(newServerPort);
    }

    @Test
    public void saveWillSaveConfiguration() throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(ConfigurationManagerTest.this.mockConfiguration).exists();
            ignoring(ConfigurationManagerTest.this.mockConfiguration)
                    .getProperty(with(any(String.class)));
            one(ConfigurationManagerTest.this.mockConfiguration).save();
        }});
        ConfigurationManager configurationManager = new ConfigurationManager(
                this.mockConfiguration);
        configurationManager.save();
    }
}
