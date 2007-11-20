package suncertify;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

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

@SuppressWarnings("boxing")
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
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockRmiService = this.context.mock(RmiService.class);
        this.mockDatabaseFactory = this.context.mock(DatabaseFactory.class);
        this.serverPort = "1189";
        this.databaseFilePath = "databaseFilePath";
        this.url = "//" + ApplicationConstants.LOCALHOST_ADDRESS + ":"
                + this.serverPort + "/"
                + ApplicationConstants.REMOTE_BROKER_SERVICE_NAME;
    }

    @After
    public void tearDown() {
        this.context.assertIsSatisfied();
    }

    @Test
    public void createConfigurationView() {
        checkingConfiguration();
        ServerApplication application = new ServerApplication(
                this.mockConfiguration, this.mockRmiService,
                this.mockDatabaseFactory);
        assertTrue(application.createConfigurationView() instanceof ServerConfigurationDialog);
    }

    private void checkingConfiguration() {
        this.context.checking(new Expectations() {
            {
                ignoring(ServerApplicationTest.this.mockConfiguration).exists();

                allowing(ServerApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.SERVER_PORT_PROPERTY)));
                will(returnValue(ServerApplicationTest.this.serverPort));

                allowing(ServerApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)));
                will(returnValue(ServerApplicationTest.this.databaseFilePath));

                allowing(ServerApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)));
            }
        });
    }

    @Test
    public void startup() throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ServerApplicationTest.this.mockRmiService)
                        .createRegistry(
                                with(equal(Integer
                                        .parseInt(ServerApplicationTest.this.serverPort))));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(equal(ServerApplicationTest.this.databaseFilePath)));

                one(ServerApplicationTest.this.mockRmiService).rebind(
                        with(equal(ServerApplicationTest.this.url)),
                        with(any(RemoteBrokerServiceImpl.class)));
            }
        });
        new ServerApplication(this.mockConfiguration, this.mockRmiService,
                this.mockDatabaseFactory).startup();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenRegistryCannotBeCreated()
            throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ServerApplicationTest.this.mockRmiService)
                        .createRegistry(
                                with(equal(Integer
                                        .parseInt(ServerApplicationTest.this.serverPort))));
                will(throwException(new RemoteException()));
            }
        });
        new ServerApplication(this.mockConfiguration, this.mockRmiService,
                this.mockDatabaseFactory).startup();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileCannotBeFound()
            throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ServerApplicationTest.this.mockRmiService)
                        .createRegistry(
                                with(equal(Integer
                                        .parseInt(ServerApplicationTest.this.serverPort))));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(equal(ServerApplicationTest.this.databaseFilePath)));
                will(throwException(new FileNotFoundException()));
            }
        });
        new ServerApplication(this.mockConfiguration, this.mockRmiService,
                this.mockDatabaseFactory).startup();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileCannotBeRead()
            throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ServerApplicationTest.this.mockRmiService)
                        .createRegistry(
                                with(equal(Integer
                                        .parseInt(ServerApplicationTest.this.serverPort))));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(equal(ServerApplicationTest.this.databaseFilePath)));
                will(throwException(new IOException()));
            }
        });
        new ServerApplication(this.mockConfiguration, this.mockRmiService,
                this.mockDatabaseFactory).startup();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenDatabaseFileIsInvalid()
            throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ServerApplicationTest.this.mockRmiService)
                        .createRegistry(
                                with(equal(Integer
                                        .parseInt(ServerApplicationTest.this.serverPort))));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(equal(ServerApplicationTest.this.databaseFilePath)));
                will(throwException(new DataValidationException("")));
            }
        });
        new ServerApplication(this.mockConfiguration, this.mockRmiService,
                this.mockDatabaseFactory).startup();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenRemoteObjectUrlIsMalformed()
            throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ServerApplicationTest.this.mockRmiService)
                        .createRegistry(
                                with(equal(Integer
                                        .parseInt(ServerApplicationTest.this.serverPort))));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(equal(ServerApplicationTest.this.databaseFilePath)));

                one(ServerApplicationTest.this.mockRmiService).rebind(
                        with(equal(ServerApplicationTest.this.url)),
                        with(any(RemoteBrokerServiceImpl.class)));
                will(throwException(new MalformedURLException()));
            }
        });
        new ServerApplication(this.mockConfiguration, this.mockRmiService,
                this.mockDatabaseFactory).startup();
    }

    @Test(expected = FatalException.class)
    public void shouldThrowFatalExceptionWhenRemoteObjectCannotBeBound()
            throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ServerApplicationTest.this.mockRmiService)
                        .createRegistry(
                                with(equal(Integer
                                        .parseInt(ServerApplicationTest.this.serverPort))));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(equal(ServerApplicationTest.this.databaseFilePath)));

                one(ServerApplicationTest.this.mockRmiService).rebind(
                        with(equal(ServerApplicationTest.this.url)),
                        with(any(RemoteBrokerServiceImpl.class)));
                will(throwException(new RemoteException()));
            }
        });
        new ServerApplication(this.mockConfiguration, this.mockRmiService,
                this.mockDatabaseFactory).startup();
    }
}
