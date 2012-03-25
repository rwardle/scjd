package suncertify;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LauncherTest {

    private final Mockery context = new Mockery();
    private Application mockApplication;
    private Launcher launcher;

    @Before
    public void setUp() {
        mockApplication = context.mock(Application.class);
        launcher = new Launcher(new StubApplicationFactory());
    }

    @After
    public void tearDown() {
        context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfCommandLineArgumentIsNull() {
        Launcher.getApplicationMode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfCommandLineArgumentIsInvalid() {
        Launcher.getApplicationMode(new String[] {"invalid-mode"});
    }

    @Test
    public void shouldUseClientApplicationModeIfNotSpecifiedOnCommandLine() {
        assertThat(Launcher.getApplicationMode(new String[0]),
                is(ApplicationMode.CLIENT));
    }

    @Test
    public void shouldUseServerApplicationModeIfCommandLineArgumentIsServer() {
        assertThat(Launcher.getApplicationMode(new String[] {"server"}),
                is(ApplicationMode.SERVER));
    }

    @Test
    public void shouldUseStandaloneApplicationModeIfCommandLineArgumentIsAlone() {
        assertThat(Launcher.getApplicationMode(new String[] {"alone"}),
                is(ApplicationMode.STANDALONE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfApplicationFactoryIsNull() {
        new Launcher(null);
    }

    @Test
    public void shouldIntialiseAndStartupApplicationWhenLaunched()
            throws Exception {
        context.checking(new Expectations() {
            {
                one(mockApplication).initialise();
                will(returnValue(true));

                one(mockApplication).startup();
            }
        });
        launcher.launch();
    }

    @Test
    public void shouldNotStartupApplicationIfInitialiseIsCancelled()
            throws Exception {
        context.checking(new Expectations() {
            {
                one(mockApplication).initialise();
                will(returnValue(false));

                never(mockApplication).startup();
            }
        });
        launcher.launch();
    }

    @Test
    public void shouldHandleExceptionWhenStartupThrowsException()
            throws Exception {
        final FatalException applicationException = new FatalException();
        context.checking(new Expectations() {
            {
                one(mockApplication).initialise();
                will(returnValue(true));

                one(mockApplication).startup();
                will(throwException(applicationException));

                one(mockApplication).handleFatalException(
                        with(is(applicationException)));
            }
        });
        launcher.launch();
    }

    private class StubApplicationFactory extends AbstractApplicationFactory {

        @Override
        public Application createApplication(Configuration configuration) {
            return mockApplication;
        }
    }
}
