package suncertify;

import org.hamcrest.CoreMatchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
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

    @After
    public void tearDown() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfCommandLineArgumentIsNull() {
        Launcher.getApplicationMode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfCommandLineArgumentIsInvalid() {
        Launcher.getApplicationMode(new String[] { "invalid-mode" });
    }

    @Test
    public void shouldUseClientApplicationModeIfNotSpecifiedOnCommandLine() {
        Assert.assertThat(Launcher.getApplicationMode(new String[0]),
                CoreMatchers.is(ApplicationMode.CLIENT));
    }

    @Test
    public void shouldUseServerApplicationModeIfCommandLineArgumentIsServer() {
        Assert.assertThat(Launcher
                .getApplicationMode(new String[] { "server" }), CoreMatchers
                .is(ApplicationMode.SERVER));
    }

    @Test
    public void shouldUseStandaloneApplicationModeIfCommandLineArgumentIsAlone() {
        Assert.assertThat(
                Launcher.getApplicationMode(new String[] { "alone" }),
                CoreMatchers.is(ApplicationMode.STANDALONE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfApplicationFactoryIsNull() {
        new Launcher(null);
    }

    @Test
    public void shouldIntialiseAndStartupApplicationWhenLaunched()
            throws Exception {
        this.context.checking(new Expectations() {
            {
                one(LauncherTest.this.mockApplication).initialise();
                will(Expectations.returnValue(true));

                one(LauncherTest.this.mockApplication).startup();
            }
        });
        this.launcher.launch();
    }

    @Test
    public void shouldNotStartupApplicationIfInitialiseIsCancelled()
            throws Exception {
        this.context.checking(new Expectations() {
            {
                one(LauncherTest.this.mockApplication).initialise();
                will(Expectations.returnValue(false));

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
                will(Expectations.returnValue(true));

                one(LauncherTest.this.mockApplication).startup();
                will(Expectations.throwException(applicationException));

                one(LauncherTest.this.mockApplication).handleFatalException(
                        with(CoreMatchers.is(applicationException)));
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
