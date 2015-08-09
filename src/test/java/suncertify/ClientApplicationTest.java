package suncertify;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import suncertify.presentation.ClientConfigurationDialog;
import suncertify.service.RemoteBrokerService;
import suncertify.service.RmiService;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static org.junit.Assert.assertTrue;

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
        mockConfiguration = context.mock(Configuration.class);
        mockRmiService = context.mock(RmiService.class);
        mockRemoteBrokerService = context.mock(RemoteBrokerService.class);
        serverAddress = "128.0.0.1";
        serverPort = "1199";
        url = "//" + serverAddress + ":" + serverPort + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;
    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
    }

    @Test
    public void shouldCreateClientConfigurationDialog() throws Exception {
        checkingConfiguration();
        ClientApplication application = new ClientApplication(mockConfiguration, mockRmiService);
        assertTrue(application.createConfigurationView() instanceof ClientConfigurationDialog);
    }

    private void checkingConfiguration() throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration).exists();
                ignoring(mockConfiguration).getProperty(
                        with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)));

                allowing(mockConfiguration).getProperty(
                        with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)));
                will(returnValue(serverAddress));

                allowing(mockConfiguration).getProperty(
                        with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)));
                will(returnValue(serverPort));
            }
        });
    }

    @Test
    public void shouldCreateRemoteBrokerService() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockRmiService).lookup(with(equal(url)));
                will(returnValue(mockRemoteBrokerService));
            }
        });

        ClientApplication application = new ClientApplication(mockConfiguration, mockRmiService);
        assertTrue(application.createBrokerService() instanceof RemoteBrokerService);
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenUrlIsMalformed() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockRmiService).lookup(with(equal(url)));
                will(throwException(new MalformedURLException()));
            }
        });
        new ClientApplication(mockConfiguration, mockRmiService).createBrokerService();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenServerCannotBeCommunicatedWith() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockRmiService).lookup(with(equal(url)));
                will(throwException(new RemoteException()));
            }
        });
        new ClientApplication(mockConfiguration, mockRmiService).createBrokerService();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenRemoveObjectIsNotBound() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockRmiService).lookup(with(equal(url)));
                will(throwException(new NotBoundException()));
            }
        });
        new ClientApplication(mockConfiguration, mockRmiService).createBrokerService();
    }
}
