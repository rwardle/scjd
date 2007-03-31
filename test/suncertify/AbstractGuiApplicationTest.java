package suncertify;

import java.util.Properties;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.expectation.AssertMo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import suncertify.presentation.ConfigurationView;
import suncertify.presentation.MainPresenter;
import suncertify.presentation.MainView;
import suncertify.service.BrokerService;

@RunWith(TestClassRunner.class)
public class AbstractGuiApplicationTest extends MockObjectTestCase {

    Mock mockPresenter;
    private AbstractGuiApplication application;

    @Before
    public void setUp() {
        this.mockPresenter = mock(MainPresenter.class,
                new Class[] {BrokerService.class, MainView.class},
                new Object[] {
                    newDummy(BrokerService.class),
                    newDummy(MainView.class),
                });
        this.application = new StubAbstractGuiApplication(
                new Configuration(new Properties()));
    }
    
    @After
    public void verify() {
        super.verify();
    }

    @Test
    public void runApplication() throws ApplicationException {
        this.mockPresenter.expects(once()).method("realiseView");
        this.application.run();
    }

    private class StubAbstractGuiApplication extends AbstractGuiApplication {

        StubAbstractGuiApplication(Configuration configuration) {
            super(configuration);
        }

        @Override
        protected ConfigurationView createConfigurationView() {
            AssertMo.notImplemented("StubAbstractGuiApplication");
            return null;
        }

        @Override
        protected BrokerService getBrokerService() {
            AssertMo.notImplemented("StubAbstractGuiApplication");
            return null;
        }

        @Override
        protected MainPresenter createMainPresenter() {
            return (MainPresenter) AbstractGuiApplicationTest.this
                    .mockPresenter.proxy();
        }
    }
}
