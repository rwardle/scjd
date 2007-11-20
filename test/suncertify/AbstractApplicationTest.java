package suncertify;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.presentation.ConfigurationPresenter;
import suncertify.presentation.ConfigurationView;

@SuppressWarnings("boxing")
public class AbstractApplicationTest {

    private final Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    private Configuration mockConfiguration;
    private ConfigurationPresenter mockPresenter;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockPresenter = this.context.mock(ConfigurationPresenter.class);
    }

    @After
    public void tearDown() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfConfigurationIsNull() {
        new StubAbstractApplication(null);
    }

    @Test
    public void initialiseSavesConfigurationWhenConfigurationIsOkayed()
            throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(AbstractApplicationTest.this.mockConfiguration)
                        .exists();
                ignoring(AbstractApplicationTest.this.mockConfiguration)
                        .getProperty(with(any(String.class)));
                ignoring(AbstractApplicationTest.this.mockConfiguration)
                        .setProperty(with(any(String.class)),
                                with(any(String.class)));
                one(AbstractApplicationTest.this.mockPresenter).realiseView();

                allowing(AbstractApplicationTest.this.mockPresenter)
                        .getReturnStatus();
                will(returnValue(ReturnStatus.OK));

                one(AbstractApplicationTest.this.mockConfiguration).save();
            }
        });
        StubAbstractApplication application = new StubAbstractApplication(
                this.mockConfiguration);
        assertThat(application.initialise(), is(true));
    }

    @Test
    public void shouldReturnFalseFromInitialiseWhenConfigurationIsCancelled()
            throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(AbstractApplicationTest.this.mockConfiguration);
                one(AbstractApplicationTest.this.mockPresenter).realiseView();

                allowing(AbstractApplicationTest.this.mockPresenter)
                        .getReturnStatus();
                will(returnValue(ReturnStatus.CANCEL));
            }
        });

        StubAbstractApplication application = new StubAbstractApplication(
                this.mockConfiguration);
        assertThat(application.initialise(), is(false));
    }

    @Test
    public void shouldReturnTrueFromInitialiseWhenConfigurationCannotBeSaved()
            throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(AbstractApplicationTest.this.mockConfiguration)
                        .exists();
                ignoring(AbstractApplicationTest.this.mockConfiguration)
                        .getProperty(with(any(String.class)));
                ignoring(AbstractApplicationTest.this.mockConfiguration)
                        .setProperty(with(any(String.class)),
                                with(any(String.class)));
                one(AbstractApplicationTest.this.mockPresenter).realiseView();

                allowing(AbstractApplicationTest.this.mockPresenter)
                        .getReturnStatus();
                will(returnValue(ReturnStatus.OK));

                one(AbstractApplicationTest.this.mockConfiguration).save();
                will(throwException(new ConfigurationException()));
            }
        });

        StubAbstractApplication application = new StubAbstractApplication(
                this.mockConfiguration);
        assertThat(application.initialise(), is(true));
    }

    private class StubAbstractApplication extends AbstractApplication {
        StubAbstractApplication(Configuration configuration) {
            super(configuration);
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
            throw new UnsupportedOperationException("startup() not implemented");
        }

        @Override
        void showSaveWarningDialog() {
            // Prevent dialog being shown in tests
        }
    }
}
