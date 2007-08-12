package suncertify;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class LauncherTest {

    private final Mockery context = new Mockery();
    private Launcher launcher;
    private Application mockApplication;

    @Before
    public void setUp() {
        this.launcher = new Launcher();
        this.mockApplication = this.context.mock(Application.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfCommandLineArgumentIsNull() {
        this.launcher.getApplicationMode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfCommandLineArgumentIsInvalid() {
        this.launcher.getApplicationMode(new String[] { "invalid-mode" });
    }

    @Test
    public void getsClientApplicationModeIfNotSpecifiedOnCommandLine() {
        assertSame(Launcher.ApplicationMode.CLIENT, 
                this.launcher.getApplicationMode(new String[0]));
    }

    @Test
    public void getsServerApplicationModeIfCommandLineArgumentIsServer() {
        assertSame(Launcher.ApplicationMode.SERVER, 
                this.launcher.getApplicationMode(new String[] { "server" }));
    }

    @Test
    public void getsStandaloneApplicationModeIfCommandLineArgumentIsAlone() {
        assertSame(Launcher.ApplicationMode.STANDALONE, 
                this.launcher.getApplicationMode(new String[] { "alone" }));
    }

    @Test(expected=IllegalArgumentException.class)
    public void throwsExceptionIfApplicationModeIsNull() {
        this.launcher.createApplication(null);
    }
    
    @Test
    public void createsClientApplicationIfApplicationModeIsClient() {
        assertTrue(this.launcher.createApplication(
                Launcher.ApplicationMode.CLIENT) instanceof ClientApplication);
    }

    @Test
    public void createsServerApplicationIfApplicationModeIsServer() {
        assertTrue(this.launcher.createApplication(
                Launcher.ApplicationMode.SERVER) instanceof ServerApplication);
    }

    @Test
    public void createsStandaloneApplicationIfApplicationModeIsStandalone() {
        assertTrue(this.launcher.createApplication(
                Launcher.ApplicationMode.STANDALONE) 
                        instanceof StandaloneApplication);
    }

    @Test
    public void launchHappyPath() throws Exception {
        this.context.checking(new Expectations() {{
            one(LauncherTest.this.mockApplication).initialise();
            one(LauncherTest.this.mockApplication).startup();
        }});
        this.launcher.launch(this.mockApplication);
    }

    @Test
    public void applicationIsShutdownWhenInitialiseThrowsException() 
            throws Exception {
        final ApplicationException applicationException 
                = new ApplicationException();
        this.context.checking(new Expectations() {{
            one(LauncherTest.this.mockApplication).initialise();
                will(throwException(applicationException));
            never(LauncherTest.this.mockApplication).startup();
            one(LauncherTest.this.mockApplication).handleException(
                    with(is(applicationException)));
            one(LauncherTest.this.mockApplication).shutdown();
        }});
        this.launcher.launch(this.mockApplication);
    }

    @Test
    public void applicationIsShutdownWhenStartupThrowsException() 
            throws Exception {
        final ApplicationException applicationException 
                = new ApplicationException();
        this.context.checking(new Expectations() {{
            one(LauncherTest.this.mockApplication).initialise();
            one(LauncherTest.this.mockApplication).startup();
                will(throwException(applicationException));
            one(LauncherTest.this.mockApplication).handleException(
                    with(is(applicationException)));
            one(LauncherTest.this.mockApplication).shutdown();
        }});
        this.launcher.launch(this.mockApplication);
    }
}
