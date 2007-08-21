package suncertify;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class LauncherTest {

    private final Mockery context = new Mockery();
    private Application mockApplication;
    private Launcher launcher;

    @Before
    public void setUp() {
        this.mockApplication = this.context.mock(Application.class);
        this.launcher = new Launcher(new StubApplicationFactory());
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
                one(LauncherTest.this.mockApplication).startup();
            }
        });
        this.launcher.launch();
    }

    @Test
    public void applicationIsShutdownWhenInitialiseThrowsException()
            throws Exception {
        final ApplicationException applicationException = new ApplicationException();
        this.context.checking(new Expectations() {
            {
                one(LauncherTest.this.mockApplication).initialise();
                will(throwException(applicationException));

                never(LauncherTest.this.mockApplication).startup();
                one(LauncherTest.this.mockApplication).handleException(
                        with(Matchers.is(applicationException)));
                one(LauncherTest.this.mockApplication).shutdown();
            }
        });
        this.launcher.launch();
    }

    @Test
    public void applicationIsShutdownWhenStartupThrowsException()
            throws Exception {
        final ApplicationException applicationException = new ApplicationException();
        this.context.checking(new Expectations() {
            {
                one(LauncherTest.this.mockApplication).initialise();

                one(LauncherTest.this.mockApplication).startup();
                will(throwException(applicationException));

                one(LauncherTest.this.mockApplication).handleException(
                        with(Matchers.is(applicationException)));
                one(LauncherTest.this.mockApplication).shutdown();
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
