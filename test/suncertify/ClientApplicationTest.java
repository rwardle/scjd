package suncertify;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import suncertify.presentation.ClientConfigurationDialog;

public class ClientApplicationTest {

    private final Mockery context = new Mockery();
    private Configuration mockConfiguration;
    private ExceptionHandler mockExceptionHandler;
    private ShutdownHandler mockShutdownHandler;
    private ClientApplication application;

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
                ignoring(ClientApplicationTest.this.mockConfiguration);
            }
        });
        this.application = new ClientApplication(this.mockConfiguration,
                this.mockExceptionHandler, this.mockShutdownHandler);
        assertTrue(this.application.createConfigurationView() instanceof ClientConfigurationDialog);
    }

    // TODO How can we test RMI lookup here?
}
