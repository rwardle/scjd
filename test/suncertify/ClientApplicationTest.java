package suncertify;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.db.Database;
import suncertify.presentation.ClientConfigurationDialog;
import suncertify.service.RemoteBrokerService;
import suncertify.service.RmiService;

public class ClientApplicationTest {

    private final Mockery context = new Mockery();
    private Configuration mockConfiguration;
    private ExceptionHandler mockExceptionHandler;
    private ShutdownHandler mockShutdownHandler;
    private RmiService mockRmiService;
    private Database mockDatabase;
    private RemoteBrokerService mockRemoteBrokerService;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockExceptionHandler = this.context.mock(ExceptionHandler.class);
        this.mockShutdownHandler = this.context.mock(ShutdownHandler.class);
        this.mockRmiService = this.context.mock(RmiService.class);
        this.mockDatabase = this.context.mock(Database.class);
        this.mockRemoteBrokerService = this.context
                .mock(RemoteBrokerService.class);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test
    public void createConfigurationView() {
        this.context.checking(new Expectations() {
            {
                ignoring(ClientApplicationTest.this.mockConfiguration);
            }
        });
        ClientApplication application = new ClientApplication(
                this.mockConfiguration, this.mockExceptionHandler,
                this.mockShutdownHandler, this.mockRmiService);
        assertTrue(application.createConfigurationView() instanceof ClientConfigurationDialog);
    }

    @Test
    public void createBrokerService() throws Exception {
        final String serverAddress = "128.0.0.1";
        final String serverPort = "1199";
        final String url = "//" + serverAddress + ":" + serverPort + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;
        this.context.checking(new Expectations() {
            {
                ignoring(ClientApplicationTest.this.mockConfiguration).exists();
                ignoring(ClientApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)));

                allowing(ClientApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)));
                will(returnValue(serverAddress));

                allowing(ClientApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)));
                will(returnValue(serverPort));

                one(ClientApplicationTest.this.mockRmiService).lookup(
                        with(equal(url)));
                will(returnValue(ClientApplicationTest.this.mockRemoteBrokerService));
            }
        });

        ClientApplication application = new ClientApplication(
                this.mockConfiguration, this.mockExceptionHandler,
                this.mockShutdownHandler, this.mockRmiService);
        assertTrue(application.createBrokerService() instanceof RemoteBrokerService);
    }
}
