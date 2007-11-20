package suncertify;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("boxing")
public class LauncherTest {

    private final Mockery context = new Mockery();
    private Application mockApplication;
    private Launcher launcher;

    @Before
    public void setUp() {
        this.mockApplication = this.context.mock(Application.class);
        this.launcher = new Launcher(new StubApplicationFactory());
    }

    @After
    public void tearDown() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfCommandLineArgumentIsNull() {
        Launcher.getApplicationMode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfCommandLineArgumentIsInvalid() {
        Launcher.getApplicationMode(new String[] { "invalid-mode" });
    }

    @Test
    public void getsClientApplicationModeIfNotSpecifiedOnCommandLine() {
        assertThat(Launcher.getApplicationMode(new String[0]),
                is(ApplicationMode.CLIENT));
    }

    @Test
    public void getsServerApplicationModeIfCommandLineArgumentIsServer() {
        assertThat(Launcher.getApplicationMode(new String[] { "server" }),
                is(ApplicationMode.SERVER));
    }

    @Test
    public void getsStandaloneApplicationModeIfCommandLineArgumentIsAlone() {
        assertThat(Launcher.getApplicationMode(new String[] { "alone" }),
                is(ApplicationMode.STANDALONE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfApplicationFactoryIsNull() {
        new Launcher(null);
    }

    @Test
    public void launchHappyPath() throws Exception {
        this.context.checking(new Expectations() {
            {
                one(LauncherTest.this.mockApplication).initialise();
                will(returnValue(true));

                one(LauncherTest.this.mockApplication).startup();
            }
        });
        this.launcher.launch();
    }

    @Test
    public void shouldNotStartupIfInitialiseIsCancelled() throws Exception {
        this.context.checking(new Expectations() {
            {
                one(LauncherTest.this.mockApplication).initialise();
                will(returnValue(false));

                never(LauncherTest.this.mockApplication).startup();
            }
        });
        this.launcher.launch();
    }

    @Test
    public void shouldHandleExceptionWhenStartupThrowsException()
            throws Exception {
        final FatalException applicationException = new FatalException();
        this.context.checking(new Expectations() {
            {
                one(LauncherTest.this.mockApplication).initialise();
                will(returnValue(true));

                one(LauncherTest.this.mockApplication).startup();
                will(throwException(applicationException));

                one(LauncherTest.this.mockApplication).handleFatalException(
                        with(is(applicationException)));
            }
        });
        this.launcher.launch();
    }

    private class StubApplicationFactory extends AbstractApplicationFactory {

        @Override
        public Application createApplication(Configuration configuration) {
            return LauncherTest.this.mockApplication;
        }
    }
}
