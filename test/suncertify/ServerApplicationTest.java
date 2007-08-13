package suncertify;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import suncertify.presentation.ServerConfigurationDialog;

public class ServerApplicationTest {

    private final Mockery context = new Mockery();
    private Configuration mockConfiguration;
    private ExceptionHandler mockExceptionHandler;
    private ShutdownHandler mockShutdownHandler;
    private ServerApplication application;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockExceptionHandler = this.context.mock(ExceptionHandler.class);
        this.mockShutdownHandler = this.context.mock(ShutdownHandler.class);
    }

    @Test
    public void createConfigurationView() {
        this.context.checking(new Expectations() {
            {
                ignoring(ServerApplicationTest.this.mockConfiguration);
            }
        });
        this.application = new ServerApplication(this.mockConfiguration,
                this.mockExceptionHandler, this.mockShutdownHandler);
        assertTrue(this.application.createConfigurationView() instanceof ServerConfigurationDialog);
    }

    // TODO How can we test RMI startup here?
}
