package suncertify;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ApplicationFactoryTest {

    private Mockery context;
    private Configuration mockConfiguration;

    @Before
    public void setUp() {
        this.context = new Mockery();
        this.mockConfiguration = this.context.mock(Configuration.class);
    }

    @After
    public void tearDown() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfModeIsNull() {
        AbstractApplicationFactory.getApplicationFactory(null);
    }

    @Test
    public void shouldReturnClientFactoryIfModeIsClient() {
        Assert
                .assertTrue(AbstractApplicationFactory
                        .getApplicationFactory(ApplicationMode.CLIENT) instanceof ClientApplicationFactory);
    }

    @Test
    public void shouldReturnServerFactoryIfModeIsServer() {
        Assert
                .assertTrue(AbstractApplicationFactory
                        .getApplicationFactory(ApplicationMode.SERVER) instanceof ServerApplicationFactory);
    }

    @Test
    public void shouldReturnStandaloneFactoryIfModeIsStandalone() {
        Assert
                .assertTrue(AbstractApplicationFactory
                        .getApplicationFactory(ApplicationMode.STANDALONE) instanceof StandaloneApplicationFactory);
    }

    @Test
    public void shouldCreateClientApplicationUsingClientFactory() {
        ignoringConfiguration();
        Assert
                .assertTrue(new ClientApplicationFactory()
                        .createApplication(this.mockConfiguration) instanceof ClientApplication);
    }

    @Test
    public void shouldCreateServerApplicationUsingServerFactory() {
        ignoringConfiguration();
        Assert
                .assertTrue(new ServerApplicationFactory()
                        .createApplication(this.mockConfiguration) instanceof ServerApplication);
    }

    @Test
    public void shouldCreateStandaloneApplicationUsingStandaloneFactory() {
        ignoringConfiguration();
        Assert
                .assertTrue(new StandaloneApplicationFactory()
                        .createApplication(this.mockConfiguration) instanceof StandaloneApplication);
    }

    private void ignoringConfiguration() {
        this.context.checking(new Expectations() {
            {
                ignoring(ApplicationFactoryTest.this.mockConfiguration);
            }
        });
    }
}
