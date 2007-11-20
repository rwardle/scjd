package suncertify;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.presentation.ClientConfigurationDialog;
import suncertify.service.RemoteBrokerService;
import suncertify.service.RmiService;

public class ClientApplicationTest {

    private final Mockery context = new Mockery();
    private Configuration mockConfiguration;
    private RmiService mockRmiService;
    private RemoteBrokerService mockRemoteBrokerService;
    private String serverAddress;
    private String serverPort;
    private String url;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockRmiService = this.context.mock(RmiService.class);
        this.mockRemoteBrokerService = this.context
                .mock(RemoteBrokerService.class);
        this.serverAddress = "128.0.0.1";
        this.serverPort = "1199";
        this.url = "//" + this.serverAddress + ":" + this.serverPort + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;
    }

    @After
    public void tearDown() {
        this.context.assertIsSatisfied();
    }

    @Test
    public void createConfigurationView() throws Exception {
        checkingConfiguration();
        ClientApplication application = new ClientApplication(
                this.mockConfiguration, this.mockRmiService);
        assertTrue(application.createConfigurationView() instanceof ClientConfigurationDialog);
    }

    private void checkingConfiguration() throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(ClientApplicationTest.this.mockConfiguration).exists();
                ignoring(ClientApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)));

                allowing(ClientApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)));
                will(returnValue(ClientApplicationTest.this.serverAddress));

                allowing(ClientApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)));
                will(returnValue(ClientApplicationTest.this.serverPort));
            }
        });
    }

    @Test
    public void createBrokerService() throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ClientApplicationTest.this.mockRmiService).lookup(
                        with(equal(ClientApplicationTest.this.url)));
                will(returnValue(ClientApplicationTest.this.mockRemoteBrokerService));
            }
        });

        ClientApplication application = new ClientApplication(
                this.mockConfiguration, this.mockRmiService);
        assertTrue(application.createBrokerService() instanceof RemoteBrokerService);
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenUrlIsMalformed() throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ClientApplicationTest.this.mockRmiService).lookup(
                        with(equal(ClientApplicationTest.this.url)));
                will(throwException(new MalformedURLException()));
            }
        });
        new ClientApplication(this.mockConfiguration, this.mockRmiService)
                .createBrokerService();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenServerCannotBeCommunicatedWith()
            throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ClientApplicationTest.this.mockRmiService).lookup(
                        with(equal(ClientApplicationTest.this.url)));
                will(throwException(new RemoteException()));
            }
        });
        new ClientApplication(this.mockConfiguration, this.mockRmiService)
                .createBrokerService();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenRemoveObjectIsNotBound()
            throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ClientApplicationTest.this.mockRmiService).lookup(
                        with(equal(ClientApplicationTest.this.url)));
                will(throwException(new NotBoundException()));
            }
        });
        new ClientApplication(this.mockConfiguration, this.mockRmiService)
                .createBrokerService();
    }
}
