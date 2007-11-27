package suncertify;

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
    public void tearDown() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfConfigurationIsNull() {
        new ConfigurationManager(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfDatabaseFilePathIsNull() throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(ConfigurationManagerTest.this.mockConfiguration);
            }
        });
        ConfigurationManager configurationManager = new ConfigurationManager(
                this.mockConfiguration);
        configurationManager.setDatabaseFilePath(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfServerAddressIsNull() throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(ConfigurationManagerTest.this.mockConfiguration);
            }
        });
        ConfigurationManager configurationManager = new ConfigurationManager(
                this.mockConfiguration);
        configurationManager.setServerAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfServerPortIsNull() throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(ConfigurationManagerTest.this.mockConfiguration);
            }
        });
        ConfigurationManager configurationManager = new ConfigurationManager(
                this.mockConfiguration);
        configurationManager.setServerPort(null);
    }

    @Test
    public void shouldNotLoadConfigurationIfItDoesNotExist() throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(ConfigurationManagerTest.this.mockConfiguration)
                        .getProperty(with(Expectations.any(String.class)));
                ignoring(ConfigurationManagerTest.this.mockConfiguration)
                        .setProperty(with(Expectations.any(String.class)),
                                with(Expectations.any(String.class)));

                allowing(ConfigurationManagerTest.this.mockConfiguration)
                        .exists();
                will(Expectations.returnValue(false));

                never(ConfigurationManagerTest.this.mockConfiguration).load();
            }
        });
        new ConfigurationManager(this.mockConfiguration);
    }

    @Test
    public void shouldLoadConfigurationIfItExists() throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(ConfigurationManagerTest.this.mockConfiguration)
                        .getProperty(with(Expectations.any(String.class)));
                ignoring(ConfigurationManagerTest.this.mockConfiguration)
                        .setProperty(with(Expectations.any(String.class)),
                                with(Expectations.any(String.class)));

                allowing(ConfigurationManagerTest.this.mockConfiguration)
                        .exists();
                will(Expectations.returnValue(true));

                one(ConfigurationManagerTest.this.mockConfiguration).load();
            }
        });
        new ConfigurationManager(this.mockConfiguration);
    }

    @Test
    public void shouldGetDefaultConfigurationIfPropertyIsNull()
            throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(ConfigurationManagerTest.this.mockConfiguration)
                        .exists();
                allowing(ConfigurationManagerTest.this.mockConfiguration)
                        .getProperty(with(Expectations.any(String.class)));
                will(Expectations.returnValue(null));
                one(ConfigurationManagerTest.this.mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)),
                                with(Expectations
                                        .equal(ApplicationConstants.DEFAULT_DATABASE_FILE_PATH)));
                one(ConfigurationManagerTest.this.mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)),
                                with(Expectations
                                        .equal(ApplicationConstants.DEFAULT_SERVER_ADDRESS)));
                one(ConfigurationManagerTest.this.mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_PORT_PROPERTY)),
                                with(Expectations
                                        .equal(ApplicationConstants.DEFAULT_SERVER_PORT
                                                .toString())));
            }
        });
        new ConfigurationManager(this.mockConfiguration);
    }

    @Test
    public void shouldUpdateConfiguration() throws Exception {
        final String newDatabaseFilePath = "newDatabaseFilePath";
        final String newServerAddress = "newServerAddress";
        final Integer newServerPort = 9999;
        this.context.checking(new Expectations() {
            {
                ignoring(ConfigurationManagerTest.this.mockConfiguration)
                        .exists();
                ignoring(ConfigurationManagerTest.this.mockConfiguration)
                        .getProperty(with(Expectations.any(String.class)));
                one(ConfigurationManagerTest.this.mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_PORT_PROPERTY)),
                                with(Expectations
                                        .equal(ApplicationConstants.DEFAULT_SERVER_PORT
                                                .toString())));
                one(ConfigurationManagerTest.this.mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)),
                                with(Expectations.equal(newDatabaseFilePath)));
                one(ConfigurationManagerTest.this.mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)),
                                with(Expectations.equal(newServerAddress)));
                one(ConfigurationManagerTest.this.mockConfiguration)
                        .setProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_PORT_PROPERTY)),
                                with(Expectations.equal(newServerPort
                                        .toString())));
            }
        });
        ConfigurationManager configurationManager = new ConfigurationManager(
                this.mockConfiguration);
        configurationManager.setDatabaseFilePath(newDatabaseFilePath);
        configurationManager.setServerAddress(newServerAddress);
        configurationManager.setServerPort(newServerPort);
    }

    @Test
    public void shouldSaveConfiguration() throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(ConfigurationManagerTest.this.mockConfiguration)
                        .exists();
                ignoring(ConfigurationManagerTest.this.mockConfiguration)
                        .getProperty(with(Expectations.any(String.class)));
                ignoring(ConfigurationManagerTest.this.mockConfiguration)
                        .setProperty(with(Expectations.any(String.class)),
                                with(Expectations.any(String.class)));
                one(ConfigurationManagerTest.this.mockConfiguration).save();
            }
        });
        ConfigurationManager configurationManager = new ConfigurationManager(
                this.mockConfiguration);
        configurationManager.save();
    }
}
