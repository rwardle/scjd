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
        mockConfiguration = context.mock(Configuration.class);
    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfConfigurationIsNull() {
        new ConfigurationManager(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfDatabaseFilePathIsNull() throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration);
            }
        });
        ConfigurationManager configurationManager = new ConfigurationManager(mockConfiguration);
        configurationManager.setDatabaseFilePath(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfServerAddressIsNull() throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration);
            }
        });
        ConfigurationManager configurationManager = new ConfigurationManager(mockConfiguration);
        configurationManager.setServerAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfServerPortIsNull() throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration);
            }
        });
        ConfigurationManager configurationManager = new ConfigurationManager(mockConfiguration);
        configurationManager.setServerPort(null);
    }

    @Test
    public void shouldNotLoadConfigurationIfItDoesNotExist() throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration).getProperty(with(any(String.class)));
                ignoring(mockConfiguration).setProperty(with(any(String.class)),
                        with(any(String.class)));

                allowing(mockConfiguration).exists();
                will(returnValue(false));

                never(mockConfiguration).load();
            }
        });
        new ConfigurationManager(mockConfiguration);
    }

    @Test
    public void shouldLoadConfigurationIfItExists() throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration).getProperty(with(any(String.class)));
                ignoring(mockConfiguration).setProperty(with(any(String.class)),
                        with(any(String.class)));

                allowing(mockConfiguration).exists();
                will(returnValue(true));

                one(mockConfiguration).load();
            }
        });
        new ConfigurationManager(mockConfiguration);
    }

    @Test
    public void shouldGetDefaultConfigurationIfPropertyIsNull() throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration).exists();
                allowing(mockConfiguration).getProperty(with(any(String.class)));
                will(returnValue(null));
                one(mockConfiguration).setProperty(
                        with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)),
                        with(equal(ApplicationConstants.DEFAULT_DATABASE_FILE_PATH)));
                one(mockConfiguration).setProperty(
                        with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)),
                        with(equal(ApplicationConstants.DEFAULT_SERVER_ADDRESS)));
                one(mockConfiguration).setProperty(
                        with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)),
                        with(equal(ApplicationConstants.DEFAULT_SERVER_PORT.toString())));
            }
        });
        new ConfigurationManager(mockConfiguration);
    }

    @Test
    public void shouldUpdateConfiguration() throws Exception {
        final String newDatabaseFilePath = "newDatabaseFilePath";
        final String newServerAddress = "newServerAddress";
        final Integer newServerPort = 9999;
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration).exists();
                ignoring(mockConfiguration).getProperty(with(any(String.class)));
                one(mockConfiguration).setProperty(
                        with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)),
                        with(equal(ApplicationConstants.DEFAULT_SERVER_PORT.toString())));
                one(mockConfiguration).setProperty(
                        with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)),
                        with(equal(newDatabaseFilePath)));
                one(mockConfiguration).setProperty(
                        with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)),
                        with(equal(newServerAddress)));
                one(mockConfiguration).setProperty(
                        with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)),
                        with(equal(newServerPort.toString())));
            }
        });
        ConfigurationManager configurationManager = new ConfigurationManager(mockConfiguration);
        configurationManager.setDatabaseFilePath(newDatabaseFilePath);
        configurationManager.setServerAddress(newServerAddress);
        configurationManager.setServerPort(newServerPort);
    }

    @Test
    public void shouldSaveConfiguration() throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration).exists();
                ignoring(mockConfiguration).getProperty(with(any(String.class)));
                ignoring(mockConfiguration).setProperty(with(any(String.class)),
                        with(any(String.class)));
                one(mockConfiguration).save();
            }
        });
        ConfigurationManager configurationManager = new ConfigurationManager(mockConfiguration);
        configurationManager.save();
    }
}
