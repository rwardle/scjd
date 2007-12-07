package suncertify;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ApplicationFactoryTest {

    private Mockery context;
    private Configuration mockConfiguration;

    @Before
    public void setUp() {
        context = new Mockery();
        mockConfiguration = context.mock(Configuration.class);
    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfModeIsNull() {
        AbstractApplicationFactory.getApplicationFactory(null);
    }

    @Test
    public void shouldReturnClientFactoryIfModeIsClient() {
        assertTrue(AbstractApplicationFactory
                .getApplicationFactory(ApplicationMode.CLIENT) instanceof ClientApplicationFactory);
    }

    @Test
    public void shouldReturnServerFactoryIfModeIsServer() {
        assertTrue(AbstractApplicationFactory
                .getApplicationFactory(ApplicationMode.SERVER) instanceof ServerApplicationFactory);
    }

    @Test
    public void shouldReturnStandaloneFactoryIfModeIsStandalone() {
        assertTrue(AbstractApplicationFactory
                .getApplicationFactory(ApplicationMode.STANDALONE) instanceof StandaloneApplicationFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNullConfigurationPassedToClientFactory() {
        ignoringConfiguration();
        new ClientApplicationFactory().createApplication(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNullConfigurationPassedToServerFactory() {
        ignoringConfiguration();
        new ServerApplicationFactory().createApplication(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNullConfigurationPassedToStandaloneFactory() {
        ignoringConfiguration();
        new StandaloneApplicationFactory().createApplication(null);
    }

    @Test
    public void shouldCreateClientApplicationUsingClientFactory() {
        ignoringConfiguration();
        assertTrue(new ClientApplicationFactory()
                .createApplication(mockConfiguration) instanceof ClientApplication);
    }

    @Test
    public void shouldCreateServerApplicationUsingServerFactory() {
        ignoringConfiguration();
        assertTrue(new ServerApplicationFactory()
                .createApplication(mockConfiguration) instanceof ServerApplication);
    }

    @Test
    public void shouldCreateStandaloneApplicationUsingStandaloneFactory() {
        ignoringConfiguration();
        assertTrue(new StandaloneApplicationFactory()
                .createApplication(mockConfiguration) instanceof StandaloneApplication);
    }

    private void ignoringConfiguration() {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration);
            }
        });
    }
}
