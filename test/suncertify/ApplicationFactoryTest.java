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
        this.context = new Mockery();
        this.mockConfiguration = this.context.mock(Configuration.class);
    }

    @After
    public void tearDown() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfApplicationModeIsNull() {
        AbstractApplicationFactory.getApplicationFactory(null);
    }

    @Test
    public void getsClientApplicationFactoryIfApplicationModeIsClient() {
        assertTrue(AbstractApplicationFactory
                .getApplicationFactory(ApplicationMode.CLIENT) instanceof ClientApplicationFactory);
    }

    @Test
    public void getsServerApplicationFactoryIfApplicationModeIsServer() {
        assertTrue(AbstractApplicationFactory
                .getApplicationFactory(ApplicationMode.SERVER) instanceof ServerApplicationFactory);
    }

    @Test
    public void getsStandaloneApplicationFactoryIfApplicationModeIsStandalone() {
        assertTrue(AbstractApplicationFactory
                .getApplicationFactory(ApplicationMode.STANDALONE) instanceof StandaloneApplicationFactory);
    }

    @Test
    public void createsClientApplication() {
        ignoringConfiguration();
        assertTrue(new ClientApplicationFactory()
                .createApplication(this.mockConfiguration) instanceof ClientApplication);
    }

    @Test
    public void createsServerApplication() {
        ignoringConfiguration();
        assertTrue(new ServerApplicationFactory()
                .createApplication(this.mockConfiguration) instanceof ServerApplication);
    }

    @Test
    public void createsStandaloneApplication() {
        ignoringConfiguration();
        assertTrue(new StandaloneApplicationFactory()
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
