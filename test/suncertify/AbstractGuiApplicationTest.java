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

    private final Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    private Configuration mockConfiguration;
    private MainPresenter mockPresenter;
    private AbstractGuiApplication application;

    @Before
    public void setUp() {
        mockConfiguration = context.mock(Configuration.class);
        mockPresenter = context.mock(MainPresenter.class);
    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
    }

    @Test
    public void startupApplicationRealisesMainView() throws FatalException {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration);
                one(mockPresenter).realiseView();
            }
        });
        application = new StubAbstractGuiApplication(mockConfiguration);
        application.startup();
    }

    private class StubAbstractGuiApplication extends AbstractGuiApplication {

        StubAbstractGuiApplication(Configuration configuration) {
            super(configuration);
        }

        @Override
        protected ConfigurationView createConfigurationView() {
            throw new UnsupportedOperationException(
                    "createConfigurationView() not implemented");
        }

        @Override
        protected BrokerService createBrokerService() {
            throw new UnsupportedOperationException(
                    "getBrokerService() not implemented");
        }

        @Override
        protected MainPresenter createMainPresenter() {
            return mockPresenter;
        }
    }
}
