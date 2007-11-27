package suncertify;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
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
    public void shouldCreateClientConfigurationDialog() throws Exception {
        checkingConfiguration();
        ClientApplication application = new ClientApplication(
                this.mockConfiguration, this.mockRmiService);
        Assert
                .assertTrue(application.createConfigurationView() instanceof ClientConfigurationDialog);
    }

    private void checkingConfiguration() throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(ClientApplicationTest.this.mockConfiguration).exists();
                ignoring(ClientApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)));

                allowing(ClientApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)));
                will(Expectations
                        .returnValue(ClientApplicationTest.this.serverAddress));

                allowing(ClientApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_PORT_PROPERTY)));
                will(Expectations
                        .returnValue(ClientApplicationTest.this.serverPort));
            }
        });
    }

    @Test
    public void shouldCreateRemoteBrokerService() throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ClientApplicationTest.this.mockRmiService)
                        .lookup(
                                with(Expectations
                                        .equal(ClientApplicationTest.this.url)));
                will(Expectations
                        .returnValue(ClientApplicationTest.this.mockRemoteBrokerService));
            }
        });

        ClientApplication application = new ClientApplication(
                this.mockConfiguration, this.mockRmiService);
        Assert
                .assertTrue(application.createBrokerService() instanceof RemoteBrokerService);
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenUrlIsMalformed() throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ClientApplicationTest.this.mockRmiService)
                        .lookup(
                                with(Expectations
                                        .equal(ClientApplicationTest.this.url)));
                will(Expectations.throwException(new MalformedURLException()));
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
                one(ClientApplicationTest.this.mockRmiService)
                        .lookup(
                                with(Expectations
                                        .equal(ClientApplicationTest.this.url)));
                will(Expectations.throwException(new RemoteException()));
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
                one(ClientApplicationTest.this.mockRmiService)
                        .lookup(
                                with(Expectations
                                        .equal(ClientApplicationTest.this.url)));
                will(Expectations.throwException(new NotBoundException()));
            }
        });
        new ClientApplication(this.mockConfiguration, this.mockRmiService)
                .createBrokerService();
    }
}
