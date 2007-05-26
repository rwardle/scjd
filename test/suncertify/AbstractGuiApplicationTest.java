package suncertify;

import java.util.Properties;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import suncertify.presentation.ConfigurationView;
import suncertify.presentation.MainPresenter;
import suncertify.service.BrokerService;

@RunWith(TestClassRunner.class)
public class AbstractGuiApplicationTest {

    private final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private MainPresenter mockPresenter;
    private AbstractGuiApplication application;

    @Before
    public void setUp() {
        this.mockPresenter = this.context.mock(MainPresenter.class);
        this.application = new StubAbstractGuiApplication(new Configuration(
                new Properties()));
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test
    public void runApplicationRealisesView() throws ApplicationException {
        this.context.checking(new Expectations() {{
            one(AbstractGuiApplicationTest.this.mockPresenter)
                    .realiseView();
        }});
        this.application.run();
    }

    private class StubAbstractGuiApplication
            extends AbstractGuiApplication {

        StubAbstractGuiApplication(Configuration configuration) {
            super(configuration);
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
