package suncertify;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import suncertify.db.DataValidationException;
import suncertify.db.DatabaseFactory;
import suncertify.presentation.ServerConfigurationDialog;
import suncertify.service.RemoteBrokerServiceImpl;
import suncertify.service.RmiService;

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
    public void shouldCreateServerConfigurationDialog() {
        checkingConfiguration();
        ServerApplication application = new ServerApplication(
                this.mockConfiguration, this.mockRmiService,
                this.mockDatabaseFactory);
        Assert
                .assertTrue(application.createConfigurationView() instanceof ServerConfigurationDialog);
    }

    private void checkingConfiguration() {
        this.context.checking(new Expectations() {
            {
                ignoring(ServerApplicationTest.this.mockConfiguration).exists();

                allowing(ServerApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_PORT_PROPERTY)));
                will(Expectations
                        .returnValue(ServerApplicationTest.this.serverPort));

                allowing(ServerApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.DATABASE_FILE_PATH_PROPERTY)));
                will(Expectations
                        .returnValue(ServerApplicationTest.this.databaseFilePath));

                allowing(ServerApplicationTest.this.mockConfiguration)
                        .getProperty(
                                with(Expectations
                                        .equal(ApplicationConstants.SERVER_ADDRESS_PROPERTY)));
            }
        });
    }

    @Test
    public void shouldStartupRmiAndCreateDatabase() throws Exception {
        checkingConfiguration();
        this.context.checking(new Expectations() {
            {
                one(ServerApplicationTest.this.mockRmiService)
                        .createRegistry(
                                with(Expectations
                                        .equal(Integer
                                                .parseInt(ServerApplicationTest.this.serverPort))));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(Expectations
                                        .equal(ServerApplicationTest.this.databaseFilePath)));

                one(ServerApplicationTest.this.mockRmiService)
                        .rebind(
                                with(Expectations
                                        .equal(ServerApplicationTest.this.url)),
                                with(Expectations
                                        .any(RemoteBrokerServiceImpl.class)));
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
                                with(Expectations
                                        .equal(Integer
                                                .parseInt(ServerApplicationTest.this.serverPort))));
                will(Expectations.throwException(new RemoteException()));
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
                                with(Expectations
                                        .equal(Integer
                                                .parseInt(ServerApplicationTest.this.serverPort))));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(Expectations
                                        .equal(ServerApplicationTest.this.databaseFilePath)));
                will(Expectations.throwException(new FileNotFoundException()));
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
                                with(Expectations
                                        .equal(Integer
                                                .parseInt(ServerApplicationTest.this.serverPort))));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(Expectations
                                        .equal(ServerApplicationTest.this.databaseFilePath)));
                will(Expectations.throwException(new IOException()));
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
                                with(Expectations
                                        .equal(Integer
                                                .parseInt(ServerApplicationTest.this.serverPort))));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(Expectations
                                        .equal(ServerApplicationTest.this.databaseFilePath)));
                will(Expectations
                        .throwException(new DataValidationException("")));
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
                                with(Expectations
                                        .equal(Integer
                                                .parseInt(ServerApplicationTest.this.serverPort))));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(Expectations
                                        .equal(ServerApplicationTest.this.databaseFilePath)));

                one(ServerApplicationTest.this.mockRmiService)
                        .rebind(
                                with(Expectations
                                        .equal(ServerApplicationTest.this.url)),
                                with(Expectations
                                        .any(RemoteBrokerServiceImpl.class)));
                will(Expectations.throwException(new MalformedURLException()));
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
                                with(Expectations
                                        .equal(Integer
                                                .parseInt(ServerApplicationTest.this.serverPort))));

                one(ServerApplicationTest.this.mockDatabaseFactory)
                        .createDatabase(
                                with(Expectations
                                        .equal(ServerApplicationTest.this.databaseFilePath)));

                one(ServerApplicationTest.this.mockRmiService)
                        .rebind(
                                with(Expectations
                                        .equal(ServerApplicationTest.this.url)),
                                with(Expectations
                                        .any(RemoteBrokerServiceImpl.class)));
                will(Expectations.throwException(new RemoteException()));
            }
        });
        new ServerApplication(this.mockConfiguration, this.mockRmiService,
                this.mockDatabaseFactory).startup();
    }
}
