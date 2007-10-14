package suncertify;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.db.DatabaseFactory;
import suncertify.presentation.StandaloneConfigurationDialog;
import suncertify.service.BrokerServiceImpl;

public class StandaloneApplicationTest {

    private final Mockery context = new Mockery();
    private Configuration mockConfiguration;
    private ExceptionHandler mockExceptionHandler;
    private ShutdownHandler mockShutdownHandler;
    private DatabaseFactory mockDatabaseFactory;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockExceptionHandler = this.context.mock(ExceptionHandler.class);
        this.mockShutdownHandler = this.context.mock(ShutdownHandler.class);
        this.mockDatabaseFactory = this.context.mock(DatabaseFactory.class);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test
    public void createConfigurationView() {
        this.context.checking(new Expectations() {
            {
                ignoring(StandaloneApplicationTest.this.mockConfiguration);
            }
        });
        StandaloneApplication application = new StandaloneApplication(
                this.mockConfiguration, this.mockExceptionHandler,
                this.mockShutdownHandler, this.mockDatabaseFactory);
        assertTrue(application.createConfigurationView() instanceof StandaloneConfigurationDialog);
    }

    @Test
    public void createBrokerService() throws Exception {
        final String databaseFilePath = "databaseFilePath";
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
                will(returnValue(databaseFilePath));

                one(StandaloneApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(with(equal(databaseFilePath)));
            }
        });
        StandaloneApplication application = new StandaloneApplication(
                this.mockConfiguration, this.mockExceptionHandler,
                this.mockShutdownHandler, this.mockDatabaseFactory);
        assertTrue(application.createBrokerService() instanceof BrokerServiceImpl);
    }
}
