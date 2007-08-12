package suncertify;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import suncertify.presentation.ConfigurationPresenter;
import suncertify.presentation.ConfigurationView;

public class AbstractApplicationTest {
    
    private final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private Configuration mockConfiguration;
    private ExceptionHandler mockExceptionHandler;
    private ShutdownHandler mockShutdownHandler;
    private ConfigurationPresenter mockPresenter;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockExceptionHandler = this.context.mock(ExceptionHandler.class);
        this.mockShutdownHandler = this.context.mock(ShutdownHandler.class);        
        this.mockPresenter 
                = this.context.mock(ConfigurationPresenter.class);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfConfigurationIsNull() {
        new StubAbstractApplication(null, this.mockExceptionHandler, 
                this.mockShutdownHandler);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfShutdownHandlerIsNull() {
        new StubAbstractApplication(this.mockConfiguration, 
                this.mockExceptionHandler, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfExceptionHandlerIsNull() {
        new StubAbstractApplication(this.mockConfiguration, null, 
                this.mockShutdownHandler);
    }

    @Test
    public void initialiseSavesConfigurationWhenConfigurationIsOkayed()
            throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(AbstractApplicationTest.this.mockConfiguration).exists();
            ignoring(AbstractApplicationTest.this.mockConfiguration)
                    .getProperty(with(a(String.class)));
            one(AbstractApplicationTest.this.mockPresenter)
                    .realiseView();
            allowing(AbstractApplicationTest.this.mockPresenter)
                    .getReturnStatus();
                will(returnValue(ReturnStatus.OK));
            one(AbstractApplicationTest.this.mockConfiguration).save();
        }});
        new StubAbstractApplication(this.mockConfiguration, 
                this.mockExceptionHandler, 
                this.mockShutdownHandler).initialise();
    }
    
    @Test
    public void initialiseShutsDownApplicationWhenConfigurationIsCancelled()
            throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(AbstractApplicationTest.this.mockConfiguration);
            one(AbstractApplicationTest.this.mockPresenter)
                    .realiseView();
            allowing(AbstractApplicationTest.this.mockPresenter)
                    .getReturnStatus();
                will(returnValue(ReturnStatus.CANCEL));
            one(AbstractApplicationTest.this.mockShutdownHandler)
                    .handleShutdown();
        }});
        new StubAbstractApplication(this.mockConfiguration, 
                this.mockExceptionHandler,
                this.mockShutdownHandler).initialise();
    }

    @Test
    public void initialiseHandlesExceptionWhenConfigurationCannotBeSaved()
            throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(AbstractApplicationTest.this.mockConfiguration).exists();
            ignoring(AbstractApplicationTest.this.mockConfiguration)
                    .getProperty(with(an(String.class)));
            one(AbstractApplicationTest.this.mockPresenter)
                    .realiseView();
            allowing(AbstractApplicationTest.this.mockPresenter)
                    .getReturnStatus();
                will(returnValue(ReturnStatus.OK));
            one(AbstractApplicationTest.this.mockConfiguration).save();
                will(throwException(new ConfigurationException()));
            one(AbstractApplicationTest.this.mockExceptionHandler)
                    .handleException(with(a(ApplicationException.class)));
        }});
        new StubAbstractApplication(this.mockConfiguration, 
                this.mockExceptionHandler,
                this.mockShutdownHandler).initialise();
    }

    private class StubAbstractApplication extends AbstractApplication {
        StubAbstractApplication(Configuration configuration,
                ExceptionHandler exceptionHandler, 
                ShutdownHandler shutdownHandler) {
            super(configuration, exceptionHandler, shutdownHandler);
        }

        @Override
        ConfigurationPresenter createConfigurationPresenter() {
            return AbstractApplicationTest.this.mockPresenter;
        }

        @Override
        protected ConfigurationView createConfigurationView() {
            throw new UnsupportedOperationException(
                    "createConfigurationView() not implemented");
        }
        
        public void startup() {
            throw new UnsupportedOperationException(
                    "startup() not implemented");
        }
    }
}
