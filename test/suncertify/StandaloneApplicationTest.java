package suncertify;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.db.DataValidationException;
import suncertify.db.DatabaseFactory;
import suncertify.presentation.StandaloneConfigurationDialog;
import suncertify.service.BrokerServiceImpl;

public class StandaloneApplicationTest {

    private final Mockery context = new Mockery();
    private Configuration mockConfiguration;
    private DatabaseFactory mockDatabaseFactory;
    private String databaseFilePath;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockDatabaseFactory = this.context.mock(DatabaseFactory.class);
        this.databaseFilePath = "databaseFilePath";
    }

    @After
    public void tearDown() {
        this.context.assertIsSatisfied();
    }

    @Test
    public void createConfigurationView() throws Exception {
        checkingConfiguration();
        StandaloneApplication application = new StandaloneApplication(
                this.mockConfiguration, this.mockDatabaseFactory);
        assertTrue(application.createConfigurationView() instanceof StandaloneConfigurationDialog);
    }

    private void checkingConfiguration() throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(StandaloneApplicationTest.this.mockConfiguration)
                        .exists();
                ignoring(StandaloneApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)));

                allowing(StandaloneApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)));
                will(returnValue("1199"));

                allowing(StandaloneApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)));
                will(returnValue(StandaloneApplicationTest.this.databaseFilePath));
            }
        });
    }

    @Test
    public void createBrokerService() throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(StandaloneApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(equal(StandaloneApplicationTest.this.databaseFilePath)));
            }
        });

        StandaloneApplication application = new StandaloneApplication(
                this.mockConfiguration, this.mockDatabaseFactory);
        assertTrue(application.createBrokerService() instanceof BrokerServiceImpl);
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileCannotBeFound()
            throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(StandaloneApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(equal(StandaloneApplicationTest.this.databaseFilePath)));
                will(throwException(new FileNotFoundException()));
            }
        });
        new StandaloneApplication(this.mockConfiguration,
                this.mockDatabaseFactory).createBrokerService();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileCannotBeRead()
            throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(StandaloneApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(equal(StandaloneApplicationTest.this.databaseFilePath)));
                will(throwException(new IOException()));
            }
        });
        new StandaloneApplication(this.mockConfiguration,
                this.mockDatabaseFactory).createBrokerService();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileIsInvalid()
            throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(StandaloneApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(equal(StandaloneApplicationTest.this.databaseFilePath)));
                will(throwException(new DataValidationException("")));
            }
        });
        new StandaloneApplication(this.mockConfiguration,
                this.mockDatabaseFactory).createBrokerService();
    }
}
