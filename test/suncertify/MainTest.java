package suncertify;

import java.io.File;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;

@RunWith(TestClassRunner.class)
public class MainTest {

    private final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private final String dummyPropertiesFilePath = "dummy-properties-file-path";
    private Main main;
    private Configuration mockConfiguration;
    private Application mockApplication;

    @Before
    public void setUp() {
        this.main = new Main();
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockApplication = this.context.mock(Application.class);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = NullPointerException.class)
    public void commandLineArgsCannotBeNull() {
        this.main.getApplicationMode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void commandLineArgsMustBeValid() {
        this.main.getApplicationMode(new String[] { "invalid-mode" });
    }

    @Test
    public void noCommandLineArgGivesClientMode() {
        Assert.assertEquals(ApplicationMode.CLIENT, this.main.getApplicationMode(new String[0]));
    }

    @Test
    public void serverCommandLineArgGivesServerMode() {
        Assert.assertEquals(ApplicationMode.SERVER, this.main
                .getApplicationMode(new String[] { "server" }));
    }

    @Test
    public void aloneCommandLineArgGivesStandaloneMode() {
        Assert.assertEquals(ApplicationMode.STANDALONE, this.main
                .getApplicationMode(new String[] { "alone" }));
    }

    @Test
    public void createWithClientModeGivesClientApplication() {
        Assert.assertTrue(this.main.createApplication(ApplicationMode.CLIENT,
                this.mockConfiguration) instanceof ClientApplication);
    }

    @Test
    public void createWithServerModeGivesServerApplication() {
        Assert.assertTrue(this.main.createApplication(ApplicationMode.SERVER,
                this.mockConfiguration) instanceof ServerApplication);
    }

    @Test
    public void createWithStandaloneModeGivesStandaloneApplication() {
        Assert.assertTrue(this.main.createApplication(ApplicationMode.STANDALONE,
                this.mockConfiguration) instanceof StandaloneApplication);
    }

    @Test(expected = NullPointerException.class)
    public void cannotConfigureNullApplication() {
        this.main.configureApplication(null, this.dummyPropertiesFilePath);
    }

    @Test(expected = NullPointerException.class)
    public void cannotConfigureWithNullPropertiesFilePath() {
        this.main.configureApplication(this.mockApplication, null);
    }

    @Test
    public void errorDialogShownWhenConfigureThrowsException() throws Exception {
        this.context.checking(new Expectations() {{
            one(MainTest.this.mockApplication).configure(with(an(File.class)));
               will(throwException(new ApplicationException()));
            one(MainTest.this.mockApplication).showErrorDialog(with(an(String.class)));
            ignoring(MainTest.this.mockApplication).exit(with(any(Integer.class)));
        }});
        this.main.configureApplication(this.mockApplication, this.dummyPropertiesFilePath);
    }

    @Test
    public void applicationExitsWhenWhenConfigureThrowsException() throws Exception {
        this.context.checking(new Expectations() {{
            one(MainTest.this.mockApplication).configure(with(an(File.class)));
               will(throwException(new ApplicationException()));
            ignoring(MainTest.this.mockApplication).showErrorDialog(with(any(String.class)));
            one(MainTest.this.mockApplication).exit(with(equal(1)));
        }});
        this.main.configureApplication(this.mockApplication, this.dummyPropertiesFilePath);
    }

    @Test
    public void applicationExitsWhenConfigureErrorDialogThrowsException() throws Exception {
        this.context.checking(new Expectations() {{
            one(MainTest.this.mockApplication).configure(with(an(File.class)));
               will(throwException(new ApplicationException()));
            one(MainTest.this.mockApplication).showErrorDialog(with(an(String.class)));
                will(throwException(new ApplicationException()));
            one(MainTest.this.mockApplication).exit(with(equal(1)));
        }});
        this.main.configureApplication(this.mockApplication, this.dummyPropertiesFilePath);
    }

    @Test
    public void applicationExitsWhenConfigureCancelled() throws Exception {
        this.context.checking(new Expectations() {{
            one(MainTest.this.mockApplication).configure(with(an(File.class)));
               will(returnValue(false));
            one(MainTest.this.mockApplication).exit(with(equal(0)));
        }});
        this.main.configureApplication(this.mockApplication, this.dummyPropertiesFilePath);
    }

    @Test
    public void applicationDoesNotExitWhenConfigureOkayed() throws Exception {
        this.context.checking(new Expectations() {{
            one(MainTest.this.mockApplication).configure(with(an(File.class)));
               will(returnValue(true));
            never(MainTest.this.mockApplication).exit(with(any(Integer.class)));
        }});
        this.main.configureApplication(this.mockApplication, this.dummyPropertiesFilePath);
    }

    @Test(expected = NullPointerException.class)
    public void cannotRunNullApplication() {
        this.main.runApplication(null);
    }

    @Test
    public void runApplication() throws Exception {
        this.context.checking(new Expectations() {{
            one(MainTest.this.mockApplication).run();
        }});
        this.main.runApplication(this.mockApplication);
    }

    @Test
    public void showErrorDialogWhenRunThrowsException() throws Exception {
        this.context.checking(new Expectations() {{
            one(MainTest.this.mockApplication).run();
               will(throwException(new ApplicationException()));
            one(MainTest.this.mockApplication).showErrorDialog(with(an(String.class)));
            ignoring(MainTest.this.mockApplication).exit(with(any(Integer.class)));
        }});
        this.main.runApplication(this.mockApplication);
    }

    @Test
    public void applicationExitsWhenRunThrowsException() throws Exception {
        this.context.checking(new Expectations() {{
            one(MainTest.this.mockApplication).run();
               will(throwException(new ApplicationException()));
            ignoring(MainTest.this.mockApplication).showErrorDialog(with(any(String.class)));
            one(MainTest.this.mockApplication).exit(with(equal(1)));
        }});
        this.main.runApplication(this.mockApplication);
    }

    @Test
    public void applicationExitsWhenRunErrorDialogThrowsException() throws Exception {
        this.context.checking(new Expectations() {{
            one(MainTest.this.mockApplication).run();
               will(throwException(new ApplicationException()));
            one(MainTest.this.mockApplication).showErrorDialog(with(an(String.class)));
                will(throwException(new ApplicationException()));
            one(MainTest.this.mockApplication).exit(with(equal(1)));
        }});
        this.main.runApplication(this.mockApplication);
    }
}
