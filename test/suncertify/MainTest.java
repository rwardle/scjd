package suncertify;

import java.io.File;
import java.util.Properties;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;

@RunWith(TestClassRunner.class)
public class MainTest extends MockObjectTestCase {

    private Main main;
    private Mock mockConfiguration;
    private Mock mockApplication;
    private final String dummyPropertiesFilePath = "dummy-properties-file-path";

    @Before
    public void setUp() {
        this.main = new Main();
        this.mockConfiguration = mock(Configuration.class,
                new Class[] {Properties.class},
                new Object[] {new Properties()});
        this.mockApplication = mock(Application.class);
    }

    @After
    public void verify() {
        super.verify();
    }

    @Test(expected=NullPointerException.class)
    public void getApplicationModeDisallowNullArgs() {
        this.main.getApplicationMode(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void getApplicationModeDisallowInvalidArg() {
        this.main.getApplicationMode(new String[] {"invalid-mode"});
    }

    @Test
    public void getApplicationModeWithNoArgs() {
        Assert.assertEquals("Application mode comparison,", ApplicationMode.CLIENT,
                this.main.getApplicationMode(new String[0]));
    }

    @Test
    public void getApplicationModeWithServerArg() {
        Assert.assertEquals("Application mode comparison,", ApplicationMode.SERVER,
                this.main.getApplicationMode(new String[] {"server"}));
    }

    @Test
    public void getApplicationModeWithAloneArg() {
        Assert.assertEquals("Application mode comparison,", ApplicationMode.STANDALONE,
                this.main.getApplicationMode(new String[] {"alone"}));
    }

    @Test
    public void createApplicationWithClientMode() {
        Assert.assertTrue("Instance of ClientApplication expected",
                this.main.createApplication(ApplicationMode.CLIENT,
                        (Configuration) this.mockConfiguration.proxy())
                instanceof ClientApplication);
    }

    @Test
    public void createApplicationWithServerMode() {
        Assert.assertTrue("Instance of ServerApplication expected",
                this.main.createApplication(ApplicationMode.SERVER,
                        (Configuration) this.mockConfiguration.proxy())
                instanceof ServerApplication);
    }

    @Test
    public void createApplicationWithStandaloneMode() {
        Assert.assertTrue("Instance of StandaloneApplication expected",
                this.main.createApplication(ApplicationMode.STANDALONE,
                        (Configuration) this.mockConfiguration.proxy())
                instanceof StandaloneApplication);
    }

    @Test(expected=NullPointerException.class)
    public void configureApplicationDisallowNullApplication() {
        this.main.configureApplication(null, this.dummyPropertiesFilePath);
    }

    @Test(expected=NullPointerException.class)
    public void configureApplicationDisallowNullPropertiesFilePath() {
        this.main.configureApplication((Application) this.mockApplication.proxy(), null);
    }

    @Test
    public void configureApplicationWhenConfigureThrowsException() {
        this.mockApplication.expects(once()).method("configure")
                .with(isA(File.class)).will(throwException(
                        new ApplicationException()));
        this.mockApplication.expects(once()).method("showErrorDialog")
                .with(isA(String.class));
        this.mockApplication.expects(once()).method("exit").with(eq(1));
        this.main.configureApplication(
                (Application) this.mockApplication.proxy(),
                this.dummyPropertiesFilePath);
    }

    @Test
    public void configureApplicationWhenErrorDialogThrowsException() {
        this.mockApplication.expects(once()).method("configure")
                .with(isA(File.class)).will(throwException(
                        new ApplicationException()));
        this.mockApplication.expects(once()).method("showErrorDialog")
                .with(isA(String.class))
                .will(throwException(new ApplicationException()));
        this.mockApplication.expects(once()).method("exit").with(eq(1));
        this.main.configureApplication(
                (Application) this.mockApplication.proxy(),
                this.dummyPropertiesFilePath);
    }

    @Test
    public void configureApplicationNotExitWhenOkayed() {
        this.mockApplication.expects(once()).method("configure")
                .with(isA(File.class))
                .will(returnValue(true));
        this.mockApplication.expects(never()).method("exit");
        this.main.configureApplication(
                (Application) this.mockApplication.proxy(),
                this.dummyPropertiesFilePath);
    }

    @Test
    public void configureApplicationExitWhenCancelled() {
        this.mockApplication.expects(once()).method("configure")
                .will(returnValue(false));
        this.mockApplication.expects(once()).method("exit").with(eq(0));
        this.main.configureApplication(
                (Application) this.mockApplication.proxy(),
                this.dummyPropertiesFilePath);
    }

    @Test(expected=NullPointerException.class)
    public void runApplicationDisallowNullApplication() {
        this.main.runApplication(null);
    }

    @Test
    public void runApplication() {
        this.mockApplication.expects(once()).method("run");
        this.main.runApplication((Application) this.mockApplication.proxy());
    }

    @Test
    public void runApplicationRunThrowsApplicationException() {
        this.mockApplication.expects(once()).method("run")
                .will(throwException(new ApplicationException()));
        this.mockApplication.expects(once()).method("showErrorDialog")
                .with(isA(String.class));
        this.mockApplication.expects(once()).method("exit")
                .with(eq(1));
        this.main.runApplication((Application) this.mockApplication.proxy());
    }

    @Test
    public void runApplicationWhenErrorDialogThrowsException() {
        this.mockApplication.expects(once()).method("run")
                .will(throwException(new ApplicationException()));
        this.mockApplication.expects(once()).method("showErrorDialog")
                .with(isA(String.class))
                .will(throwException(new ApplicationException()));
        this.mockApplication.expects(once()).method("exit")
                .with(eq(1));
        this.main.runApplication((Application) this.mockApplication.proxy());
    }
}
