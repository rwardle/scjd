package suncertify;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import suncertify.db.DataValidationException;
import suncertify.db.DatabaseFactory;
import suncertify.presentation.ServerConfigurationDialog;
import suncertify.service.RemoteBrokerServiceImpl;
import suncertify.service.RmiService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import static org.junit.Assert.assertTrue;

public class ServerApplicationTest {

    private final Mockery context = new Mockery();
    private Configuration mockConfiguration;
    private RmiService mockRmiService;
    private DatabaseFactory mockDatabaseFactory;
    private String serverPort;
    private String databaseFilePath;
    private String url;

    @Before
    public void setUp() {
        mockConfiguration = context.mock(Configuration.class);
        mockRmiService = context.mock(RmiService.class);
        mockDatabaseFactory = context.mock(DatabaseFactory.class);
        serverPort = "1189";
        databaseFilePath = "databaseFilePath";
        url = "//" + ApplicationConstants.LOCALHOST_ADDRESS + ":" + serverPort + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;
    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
    }

    @Test
    public void shouldCreateServerConfigurationDialog() {
        checkingConfiguration();
        ServerApplication application = new ServerApplication(mockConfiguration, mockRmiService,
                mockDatabaseFactory);
        assertTrue(application.createConfigurationView() instanceof ServerConfigurationDialog);
    }

    private void checkingConfiguration() {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration).exists();

                allowing(mockConfiguration).getProperty(
                        with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)));
                will(returnValue(serverPort));

                allowing(mockConfiguration).getProperty(
                        with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)));
                will(returnValue(databaseFilePath));

                allowing(mockConfiguration).getProperty(
                        with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)));
            }
        });
    }

    @Test
    public void shouldStartupRmiAndCreateDatabase() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockRmiService).createRegistry(with(equal(Integer.parseInt(serverPort))));

                one(mockDatabaseFactory).createDatabase(with(equal(databaseFilePath)));

                one(mockRmiService).rebind(with(equal(url)),
                        with(any(RemoteBrokerServiceImpl.class)));
            }
        });
        new ServerApplication(mockConfiguration, mockRmiService, mockDatabaseFactory).startup();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenRegistryCannotBeCreated() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockRmiService).createRegistry(with(equal(Integer.parseInt(serverPort))));
                will(throwException(new RemoteException()));
            }
        });
        new ServerApplication(mockConfiguration, mockRmiService, mockDatabaseFactory).startup();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileCannotBeFound() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockRmiService).createRegistry(with(equal(Integer.parseInt(serverPort))));

                one(mockDatabaseFactory).createDatabase(with(equal(databaseFilePath)));
                will(throwException(new FileNotFoundException()));
            }
        });
        new ServerApplication(mockConfiguration, mockRmiService, mockDatabaseFactory).startup();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileCannotBeRead() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockRmiService).createRegistry(with(equal(Integer.parseInt(serverPort))));

                one(mockDatabaseFactory).createDatabase(with(equal(databaseFilePath)));
                will(throwException(new IOException()));
            }
        });
        new ServerApplication(mockConfiguration, mockRmiService, mockDatabaseFactory).startup();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileIsInvalid() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockRmiService).createRegistry(with(equal(Integer.parseInt(serverPort))));

                one(mockDatabaseFactory).createDatabase(with(equal(databaseFilePath)));
                will(throwException(new DataValidationException("")));
            }
        });
        new ServerApplication(mockConfiguration, mockRmiService, mockDatabaseFactory).startup();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenRemoteObjectUrlIsMalformed() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockRmiService).createRegistry(with(equal(Integer.parseInt(serverPort))));

                one(mockDatabaseFactory).createDatabase(with(equal(databaseFilePath)));

                one(mockRmiService).rebind(with(equal(url)),
                        with(any(RemoteBrokerServiceImpl.class)));
                will(throwException(new MalformedURLException()));
            }
        });
        new ServerApplication(mockConfiguration, mockRmiService, mockDatabaseFactory).startup();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenRemoteObjectCannotBeBound() throws Exception {
        checkingConfiguration();
        context.checking(new Expectations() {
            {
                one(mockRmiService).createRegistry(with(equal(Integer.parseInt(serverPort))));

                one(mockDatabaseFactory).createDatabase(with(equal(databaseFilePath)));

                one(mockRmiService).rebind(with(equal(url)),
                        with(any(RemoteBrokerServiceImpl.class)));
                will(throwException(new RemoteException()));
            }
        });
        new ServerApplication(mockConfiguration, mockRmiService, mockDatabaseFactory).startup();
    }
}
