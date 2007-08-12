package suncertify;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import suncertify.presentation.ConfigurationView;
import suncertify.presentation.MainPresenter;
import suncertify.service.BrokerService;

public class AbstractGuiApplicationTest {

    private final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private Configuration mockConfiguration;
    private ExceptionHandler mockExceptionHandler;
    private ShutdownHandler mockShutdownHandler;
    private MainPresenter mockPresenter;
    private AbstractGuiApplication application;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockExceptionHandler = this.context.mock(ExceptionHandler.class);
        this.mockShutdownHandler = this.context.mock(ShutdownHandler.class);
        this.mockPresenter = this.context.mock(MainPresenter.class);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test
    public void startupApplicationRealisesMainView() 
            throws ApplicationException {
        this.context.checking(new Expectations() {{
            ignoring(AbstractGuiApplicationTest.this.mockConfiguration);
            one(AbstractGuiApplicationTest.this.mockPresenter)
                    .realiseView();
        }});
        this.application = new StubAbstractGuiApplication(this.mockConfiguration, 
                this.mockExceptionHandler, this.mockShutdownHandler);
        this.application.startup();
    }

    // TODO Add shutdown test
    
    private class StubAbstractGuiApplication extends AbstractGuiApplication {

        StubAbstractGuiApplication(Configuration configuration, 
                ExceptionHandler exceptionHandler, 
                ShutdownHandler shutdownHandler) {
            super(configuration, exceptionHandler, shutdownHandler);
        }

        @Override
        protected ConfigurationView createConfigurationView() {
            throw new UnsupportedOperationException(
                    "createConfigurationView() not implemented");
        }

        @Override
        protected BrokerService getBrokerService() {
            throw new UnsupportedOperationException(
                    "getBrokerService() not implemented");
        }

        @Override
        protected MainPresenter createMainPresenter() {
            return AbstractGuiApplicationTest.this.mockPresenter;
        }
    }
}
