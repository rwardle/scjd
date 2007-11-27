package suncertify;

import org.hamcrest.CoreMatchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import suncertify.presentation.ConfigurationPresenter;
import suncertify.presentation.ConfigurationView;

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
    public void shouldThrowExceptionIfConfigurationIsNull() {
        new StubAbstractApplication(null);
    }

    @Test
    public void shouldReturnTrueFromInitialiseWhenConfigurationIsOkayed()
            throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(AbstractApplicationTest.this.mockConfiguration)
                        .exists();
                ignoring(AbstractApplicationTest.this.mockConfiguration)
                        .getProperty(with(Expectations.any(String.class)));
                ignoring(AbstractApplicationTest.this.mockConfiguration)
                        .setProperty(with(Expectations.any(String.class)),
                                with(Expectations.any(String.class)));
                one(AbstractApplicationTest.this.mockPresenter).realiseView();

                allowing(AbstractApplicationTest.this.mockPresenter)
                        .getReturnStatus();
                will(Expectations.returnValue(ReturnStatus.OK));

                one(AbstractApplicationTest.this.mockConfiguration).save();
            }
        });
        StubAbstractApplication application = new StubAbstractApplication(
                this.mockConfiguration);
        Assert.assertThat(application.initialise(), CoreMatchers.is(true));
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
                will(Expectations.returnValue(ReturnStatus.CANCEL));
            }
        });

        StubAbstractApplication application = new StubAbstractApplication(
                this.mockConfiguration);
        Assert.assertThat(application.initialise(), CoreMatchers.is(false));
    }

    @Test
    public void shouldReturnTrueFromInitialiseWhenConfigurationCannotBeSaved()
            throws Exception {
        this.context.checking(new Expectations() {
            {
                ignoring(AbstractApplicationTest.this.mockConfiguration)
                        .exists();
                ignoring(AbstractApplicationTest.this.mockConfiguration)
                        .getProperty(with(Expectations.any(String.class)));
                ignoring(AbstractApplicationTest.this.mockConfiguration)
                        .setProperty(with(Expectations.any(String.class)),
                                with(Expectations.any(String.class)));
                one(AbstractApplicationTest.this.mockPresenter).realiseView();

                allowing(AbstractApplicationTest.this.mockPresenter)
                        .getReturnStatus();
                will(Expectations.returnValue(ReturnStatus.OK));

                one(AbstractApplicationTest.this.mockConfiguration).save();
                will(Expectations.throwException(new ConfigurationException()));
            }
        });

        StubAbstractApplication application = new StubAbstractApplication(
                this.mockConfiguration);
        Assert.assertThat(application.initialise(), CoreMatchers.is(true));
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
