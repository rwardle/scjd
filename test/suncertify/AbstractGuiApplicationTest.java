/*
 * AbstractGuiApplicationTest.java
 *
 * Created on 12-Jul-2005
 */


package suncertify;

import java.util.Properties;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.expectation.AssertMo;

import suncertify.presentation.ConfigurationView;
import suncertify.presentation.MainPresenter;
import suncertify.presentation.MainView;
import suncertify.service.BrokerService;


/**
 * Unit tests for {@link suncertify.AbstractGuiApplication}.
 *
 * @author Richard Wardle
 */
public final class AbstractGuiApplicationTest extends MockObjectTestCase {

    Mock mockPresenter;
    private AbstractGuiApplication application;

    /**
     * Creates a new instance of <code>AbstractGuiApplicationTest</code>.
     */
    public AbstractGuiApplicationTest() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() {
        this.application = new StubAbstractGuiApplication(
                new Configuration(new Properties()));
        this.mockPresenter = mock(MainPresenter.class,
                new Class[] {BrokerService.class, MainView.class},
                new Object[] {
                    newDummy(BrokerService.class),
                    newDummy(MainView.class),
                });
    }

    /**
     * Should initialise and realise the view.
     *
     * @throws ApplicationException If there is an error running the
     * application.
     */
    public void testRun() throws ApplicationException {
        this.mockPresenter.expects(once()).method("initialiseView");
        this.mockPresenter.expects(once()).method("realiseView");
        this.application.run();
    }

    private class StubAbstractGuiApplication extends AbstractGuiApplication {

        StubAbstractGuiApplication(Configuration configuration) {
            super(configuration);
        }

        /**
         * {@inheritDoc}
         */
        protected ConfigurationView createConfigurationView() {
            AssertMo.notImplemented("StubAbstractGuiApplication");
            return null;
        }

        /**
         * {@inheritDoc}
         */
        protected BrokerService getBrokerService() {
            AssertMo.notImplemented("StubAbstractGuiApplication");
            return null;
        }

        /**
         * {@inheritDoc}
         */
        protected MainPresenter createMainPresenter() {
            return (MainPresenter) AbstractGuiApplicationTest.this
                    .mockPresenter.proxy();
        }
    }
}
