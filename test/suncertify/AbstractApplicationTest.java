package suncertify;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import suncertify.presentation.ConfigurationPresenter;
import suncertify.presentation.ConfigurationView;

//@RunWith(TestClassRunner.class)
public class AbstractApplicationTest {

    private final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private final String dummyPropertiesFilePath = "dummy-properties-file-path";
    private ConfigurationView mockView;
    private Configuration mockConfiguration;
    private ConfigurationPresenter mockPresenter;
    private AbstractApplication application;

    @Before
    public void setUp() {
        this.mockView = this.context.mock(ConfigurationView.class);
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.mockPresenter = this.context.mock(ConfigurationPresenter.class);
        this.application = new StubAbstractApplication(this.mockConfiguration);
    }

    @After
    public void tearDown() {
        File dummyPropertiesFile = new File(this.dummyPropertiesFilePath);
        if (dummyPropertiesFile.exists()) {
            dummyPropertiesFile.delete();
        }
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test(expected = NullPointerException.class)
    public void cannotBeConstructedWithNullConfiguration() {
        new StubAbstractApplication(null);
    }

    @Test(expected = NullPointerException.class)
    public void cannotBeConfiguredWithNullPropertiesFile() throws Exception {
        this.application.configure(null);
    }

    @Test
    public void configurationNotLoadedWhenPropertiesFileDoesNotExist()
            throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(AbstractApplicationTest.this.mockPresenter);
            never(AbstractApplicationTest.this.mockConfiguration)
                    .loadConfiguration(with(any(InputStream.class)));
        }});
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    @Test
    public void configurationLoadedWhenPropertiesFileExists() throws Exception {
        File propertiesFile = new File(this.dummyPropertiesFilePath);
        propertiesFile.createNewFile();
        this.context.checking(new Expectations() {{
            ignoring(AbstractApplicationTest.this.mockPresenter);
            one(AbstractApplicationTest.this.mockConfiguration)
                    .loadConfiguration(with(an(InputStream.class)));
        }});
        this.application.configure(propertiesFile);
    }

    @Test
    public void configureReturnsFalseWhenDialogCancelled() throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(AbstractApplicationTest.this.mockPresenter)
                    .realiseView();
            one(AbstractApplicationTest.this.mockPresenter)
                    .getReturnStatus();
                will(returnValue(ConfigurationPresenter.RETURN_CANCEL));
        }});
        Assert.assertFalse(this.application.configure(new File(
                this.dummyPropertiesFilePath)));
    }

    @Test
    public void configurationNotSavedWhenDialogCancelled() throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(AbstractApplicationTest.this.mockPresenter)
                    .realiseView();
            one(AbstractApplicationTest.this.mockPresenter)
                    .getReturnStatus();
                will(returnValue(ConfigurationPresenter.RETURN_CANCEL));
            never(AbstractApplicationTest.this.mockConfiguration)
                    .saveConfiguration(with(any(OutputStream.class)));
        }});
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    @Test
    public void configureReturnsTrueWhenDialogOkayed() throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(AbstractApplicationTest.this.mockPresenter)
                    .realiseView();
            one(AbstractApplicationTest.this.mockPresenter)
                    .getReturnStatus();
                will(returnValue(ConfigurationPresenter.RETURN_OK));
            ignoring(AbstractApplicationTest.this.mockConfiguration);
        }});
        Assert.assertTrue("User should OK configuration process",
                this.application.configure(new File(
                        this.dummyPropertiesFilePath)));
    }

    @Test
    public void configurationSavedWhenDialogOkayed() throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(AbstractApplicationTest.this.mockPresenter)
                    .realiseView();
            one(AbstractApplicationTest.this.mockPresenter)
                    .getReturnStatus();
                will(returnValue(ConfigurationPresenter.RETURN_OK));
            one(AbstractApplicationTest.this.mockConfiguration)
                    .saveConfiguration(with(an(OutputStream.class)));
        }});
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    @Test
    public void configureDisplaysView() throws Exception {
        this.context.checking(new Expectations() {{
            one(AbstractApplicationTest.this.mockPresenter).realiseView();
            ignoring(AbstractApplicationTest.this.mockPresenter)
                    .getReturnStatus();
        }});
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    @Test(expected = ApplicationException.class)
    public void configureTerminatesWhenLoadConfigurationFails()
            throws Exception {
        File propertiesFile = new File(this.dummyPropertiesFilePath);
        propertiesFile.createNewFile();
        this.context.checking(new Expectations() {{
            one(AbstractApplicationTest.this.mockConfiguration)
                    .loadConfiguration(with(an(InputStream.class)));
                will(throwException(new IOException()));
            never(AbstractApplicationTest.this.mockConfiguration)
                    .saveConfiguration(with(any(OutputStream.class)));
            never(AbstractApplicationTest.this.mockPresenter)
                    .getReturnStatus();
        }});
        this.application.configure(propertiesFile);
    }

    @Test(expected = ApplicationException.class)
    public void configureTerminatesWhenSaveConfigurationFails()
            throws Exception {
        this.context.checking(new Expectations() {{
            ignoring(AbstractApplicationTest.this.mockPresenter)
                    .realiseView();
            one(AbstractApplicationTest.this.mockPresenter)
                    .getReturnStatus();
                will(returnValue(ConfigurationPresenter.RETURN_OK));
            one(AbstractApplicationTest.this.mockConfiguration)
                    .saveConfiguration(with(an(OutputStream.class)));
                will(throwException(new IOException()));
        }});
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    private class StubAbstractApplication
            extends AbstractApplication {

        StubAbstractApplication(Configuration configuration) {
            super(configuration);
        }

        @Override
        protected ConfigurationPresenter createConfigurationPresenter() {
            return AbstractApplicationTest.this.mockPresenter;
        }

        @Override
        protected ConfigurationView createConfigurationView() {
            return AbstractApplicationTest.this.mockView;
        }

        public void run() {
            throw new UnsupportedOperationException("run() not implemented");
        }
    }
}
