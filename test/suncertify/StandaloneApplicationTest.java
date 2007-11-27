package suncertify;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
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
    public void shouldCreateStandaloneConfigurationDialog() throws Exception {
        checkingConfiguration();
        StandaloneApplication application = new StandaloneApplication(
                this.mockConfiguration, this.mockDatabaseFactory);
        Assert
                .assertTrue(application.createConfigurationView() instanceof StandaloneConfigurationDialog);
    }

    private void checkingConfiguration() throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(StandaloneApplicationTest.this.mockConfiguration)
                        .exists();
                ignoring(StandaloneApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)));

                allowing(StandaloneApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_PORT_PROPERTY)));
                will(Expectations.returnValue("1199"));

                allowing(StandaloneApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)));
                will(Expectations
                        .returnValue(StandaloneApplicationTest.this.databaseFilePath));
            }
        });
    }

    @Test
    public void shouldCreateLocalBrokerService() throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(StandaloneApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(Expectations
                                        .equal(StandaloneApplicationTest.this.databaseFilePath)));
            }
        });

        StandaloneApplication application = new StandaloneApplication(
                this.mockConfiguration, this.mockDatabaseFactory);
        Assert
                .assertTrue(application.createBrokerService() instanceof BrokerServiceImpl);
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileCannotBeFound()
            throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(StandaloneApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(Expectations
                                        .equal(StandaloneApplicationTest.this.databaseFilePath)));
                will(Expectations.throwException(new FileNotFoundException()));
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
                                with(Expectations
                                        .equal(StandaloneApplicationTest.this.databaseFilePath)));
                will(Expectations.throwException(new IOException()));
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
                                with(Expectations
                                        .equal(StandaloneApplicationTest.this.databaseFilePath)));
                will(Expectations
                        .throwException(new DataValidationException("")));
            }
        });
        new StandaloneApplication(this.mockConfiguration,
                this.mockDatabaseFactory).createBrokerService();
    }
}
