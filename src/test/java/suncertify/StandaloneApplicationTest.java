package suncertify;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import suncertify.db.DataValidationException;
import suncertify.db.DatabaseFactory;
import suncertify.presentation.StandaloneConfigurationDialog;
import suncertify.service.BrokerServiceImpl;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class StandaloneApplicationTest {

    private final Mockery context = new Mockery();
    private Configuration mockConfiguration;
    private DatabaseFactory mockDatabaseFactory;
    private String databaseFilePath;

    @Before
    public void setUp() {
        mockConfiguration = context.mock(Configuration.class);
        mockDatabaseFactory = context.mock(DatabaseFactory.class);
        databaseFilePath = "databaseFilePath";
    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
    }

    @Test
    public void shouldCreateStandaloneConfigurationDialog() throws Exception {
        checkingConfiguration();
        StandaloneApplication application = new StandaloneApplication(mockConfiguration,
                mockDatabaseFactory);
        assertTrue(application.createConfigurationView() instanceof StandaloneConfigurationDialog);
    }

    private void checkingConfiguration() throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration).exists();
                ignoring(mockConfiguration).getProperty(
                        with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)));

                allowing(mockConfiguration).getProperty(
                        with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)));
                will(returnValue("1199"));

                allowing(mockConfiguration).getProperty(
                        with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)));
                will(returnValue(databaseFilePath));
            }
        });
    }

    @Test
    public void shouldCreateLocalBrokerService() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockDatabaseFactory).createDatabase(with(equal(databaseFilePath)));
            }
        });

        StandaloneApplication application = new StandaloneApplication(mockConfiguration,
                mockDatabaseFactory);
        assertTrue(application.createBrokerService() instanceof BrokerServiceImpl);
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileCannotBeFound() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockDatabaseFactory).createDatabase(with(equal(databaseFilePath)));
                will(throwException(new FileNotFoundException()));
            }
        });
        new StandaloneApplication(mockConfiguration, mockDatabaseFactory).createBrokerService();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileCannotBeRead() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockDatabaseFactory).createDatabase(with(equal(databaseFilePath)));
                will(throwException(new IOException()));
            }
        });
        new StandaloneApplication(mockConfiguration, mockDatabaseFactory).createBrokerService();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileIsInvalid() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockDatabaseFactory).createDatabase(with(equal(databaseFilePath)));
                will(throwException(new DataValidationException("")));
            }
        });
        new StandaloneApplication(mockConfiguration, mockDatabaseFactory).createBrokerService();
    }
}
