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
        mockConfiguration = context.mock(Configuration.class);
        mockPresenter = context.mock(ConfigurationPresenter.class);
    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfConfigurationIsNull() {
        new StubAbstractApplication(null);
    }

    @Test
    public void shouldReturnTrueFromInitialiseWhenConfigurationIsOkayed()
            throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration).exists();
                ignoring(mockConfiguration)
                        .getProperty(with(any(String.class)));
                ignoring(mockConfiguration).setProperty(
                        with(any(String.class)), with(any(String.class)));
                one(mockPresenter).realiseView();

                allowing(mockPresenter).getReturnStatus();
                will(returnValue(ReturnStatus.OK));

                one(mockConfiguration).save();
            }
        });
        StubAbstractApplication application = new StubAbstractApplication(
                mockConfiguration);
        assertThat(application.initialise(), is(true));
    }

    @Test
    public void shouldReturnFalseFromInitialiseWhenConfigurationIsCancelled()
            throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration);
                one(mockPresenter).realiseView();

                allowing(mockPresenter).getReturnStatus();
                will(returnValue(ReturnStatus.CANCEL));
            }
        });

        StubAbstractApplication application = new StubAbstractApplication(
                mockConfiguration);
        assertThat(application.initialise(), is(false));
    }

    @Test
    public void shouldReturnTrueFromInitialiseWhenConfigurationCannotBeSaved()
            throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration).exists();
                ignoring(mockConfiguration)
                        .getProperty(with(any(String.class)));
                ignoring(mockConfiguration).setProperty(
                        with(any(String.class)), with(any(String.class)));
                one(mockPresenter).realiseView();

                allowing(mockPresenter).getReturnStatus();
                will(returnValue(ReturnStatus.OK));

                one(mockConfiguration).save();
                will(throwException(new ConfigurationException()));
            }
        });

        StubAbstractApplication application = new StubAbstractApplication(
                mockConfiguration);
        assertThat(application.initialise(), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfFatalExceptionIsNull() throws Exception {
        context.checking(new Expectations() {
            {
                ignoring(mockConfiguration).exists();
                ignoring(mockConfiguration)
                        .getProperty(with(any(String.class)));
                ignoring(mockConfiguration).setProperty(
                        with(any(String.class)), with(any(String.class)));
            }
        });

        new StubAbstractApplication(mockConfiguration)
                .handleFatalException(null);
    }

    private class StubAbstractApplication extends AbstractApplication {
        StubAbstractApplication(Configuration configuration) {
            super(configuration);
        }

        @Override
        ConfigurationPresenter createConfigurationPresenter() {
            return mockPresenter;
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
