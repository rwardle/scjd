package suncertify;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.presentation.StandaloneConfigurationDialog;
import suncertify.service.BrokerServiceImpl;

public class StandaloneApplicationTest {

    private final Mockery context = new Mockery();
    private Configuration mockConfiguration;
    private ExceptionHandler mockExceptionHandler;
    private ShutdownHandler mockShutdownHandler;
    private StandaloneApplication application;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockExceptionHandler = this.context.mock(ExceptionHandler.class);
        this.mockShutdownHandler = this.context.mock(ShutdownHandler.class);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test
    public void createConfigurationView() {
        this.context.checking(new Expectations() {
            {
                ignoring(StandaloneApplicationTest.this.mockConfiguration);
            }
        });
        this.application = new StandaloneApplication(this.mockConfiguration,
                this.mockExceptionHandler, this.mockShutdownHandler);
        assertTrue(this.application.createConfigurationView() instanceof StandaloneConfigurationDialog);
    }

    @Test
    public void getBrokerService() {
        this.context.checking(new Expectations() {
            {
                ignoring(StandaloneApplicationTest.this.mockConfiguration);
            }
        });
        this.application = new StandaloneApplication(this.mockConfiguration,
                this.mockExceptionHandler, this.mockShutdownHandler);
        assertTrue(this.application.getBrokerService() instanceof BrokerServiceImpl);
    }
}
