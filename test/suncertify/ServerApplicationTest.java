package suncertify;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.db.DatabaseFactory;
import suncertify.presentation.ServerConfigurationDialog;
import suncertify.service.RemoteBrokerServiceImpl;
import suncertify.service.RmiService;

public class ServerApplicationTest {

    private final Mockery context = new Mockery();
    private Configuration mockConfiguration;
    private ExceptionHandler mockExceptionHandler;
    private ShutdownHandler mockShutdownHandler;
    private RmiService mockRmiService;
    private DatabaseFactory mockDatabaseFactory;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockExceptionHandler = this.context.mock(ExceptionHandler.class);
        this.mockShutdownHandler = this.context.mock(ShutdownHandler.class);
        this.mockRmiService = this.context.mock(RmiService.class);
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
                ignoring(ServerApplicationTest.this.mockConfiguration);
            }
        });
        ServerApplication application = new ServerApplication(
                this.mockConfiguration, this.mockExceptionHandler,
                this.mockShutdownHandler, this.mockRmiService,
                this.mockDatabaseFactory);
        assertTrue(application.createConfigurationView() instanceof ServerConfigurationDialog);
    }

    @Test
    public void startup() throws Exception {
        final String serverPort = "1189";
        final String databaseFilePath = "databaseFilePath";
        final String url = "//" + ApplicationConstants.LOCALHOST_ADDRESS + ":"
                + serverPort + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;
        this.context.checking(new Expectations() {
            {
                ignoring(ServerApplicationTest.this.mockConfiguration).exists();
                ignoring(ServerApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)));

                allowing(ServerApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)));
                will(returnValue(serverPort));

                one(ServerApplicationTest.this.mockRmiService).createRegistry(
                        with(equal(Integer.parseInt(serverPort))));

                allowing(ServerApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)));
                will(returnValue(databaseFilePath));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(with(equal(databaseFilePath)));

                one(ServerApplicationTest.this.mockRmiService).rebind(
                        with(equal(url)),
                        with(any(RemoteBrokerServiceImpl.class)));
            }
        });

        new ServerApplication(this.mockConfiguration,
                this.mockExceptionHandler, this.mockShutdownHandler,
                this.mockRmiService, this.mockDatabaseFactory).startup();
    }
}
