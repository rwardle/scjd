package suncertify;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.expectation.AssertMo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import suncertify.presentation.ConfigurationPresenter;
import suncertify.presentation.ConfigurationView;

@RunWith(TestClassRunner.class)
public class AbstractApplicationTest extends MockObjectTestCase {

    Mock mockPresenter;
    Mock mockView;
    private AbstractApplication application;
    private String dummyPropertiesFilePath;
    private Mock mockConfiguration;

    @Before
    public void setUp() {
        this.dummyPropertiesFilePath = "dummy-properties-file-path";
        this.mockConfiguration = mock(Configuration.class,
                new Class[] {Properties.class},
                new Object[] {new Properties()});
        this.application = new StubAbstractApplication(
                (Configuration) this.mockConfiguration.proxy());
        this.mockView = mock(ConfigurationView.class);
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
        super.verify();
    }

    private void setUpDefaultExpectations() {
        this.mockPresenter = mock(ConfigurationPresenter.class,
                new Class[] {Configuration.class, ConfigurationView.class},
                new Object[] {
                    (Configuration) this.mockConfiguration.proxy(),
                    (ConfigurationView) this.mockView.proxy(),
                });
        this.mockPresenter.stubs().method(eq("initialiseView"));
        this.mockPresenter.stubs().method(eq("realiseView"));
        this.mockPresenter.stubs().method(eq("getReturnStatus"))
                .will(returnValue(ConfigurationPresenter.RETURN_CANCEL));
        this.mockConfiguration.stubs().method("loadConfiguration");
        this.mockConfiguration.stubs().method("saveConfiguration");
    }

    @Test(expected=NullPointerException.class)
    public void constructorDisallowNullConfiguration() {
        new StubAbstractApplication(null);
    }

    @Test(expected=NullPointerException.class)
    public void configureDisallowNullPropertiesFile() throws Exception {
        this.application.configure(null);
    }

    @Test
    public void configureNotLoadConfigurationWhenPropertiesFileNotExist()
            throws ApplicationException {
        setUpDefaultExpectations();
        this.mockConfiguration.expects(never()).method("loadConfiguration");
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    @Test
    public void configureLoadConfigurationWhenPropertiesFileExists() throws Exception {
        setUpDefaultExpectations();
        File propertiesFile = new File(this.dummyPropertiesFilePath);
        propertiesFile.createNewFile();
        this.mockConfiguration.expects(once()).method("loadConfiguration")
                .with(isA(InputStream.class));
        this.application.configure(propertiesFile);
    }

    @Test
    public void configureReturnFalseWhenCancelled() throws Exception {
        setUpDefaultExpectations();
        this.mockPresenter.expects(once()).method("getReturnStatus")
                .will(returnValue(ConfigurationPresenter.RETURN_CANCEL));
        Assert.assertFalse("User should cancel configuration process",
                this.application.configure(
                        new File(this.dummyPropertiesFilePath)));
    }

    @Test
    public void configureNotSaveConfigurationWhenCancelled() throws Exception {
        setUpDefaultExpectations();
        this.mockConfiguration.expects(never()).method("saveConfiguration");
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    @Test
    public void configureReturnTrueWhenOkayed() throws Exception {
        setUpDefaultExpectations();
        this.mockPresenter.expects(once()).method(eq("getReturnStatus"))
                .will(returnValue(ConfigurationPresenter.RETURN_OK));
        Assert.assertTrue("User should OK configuration process",
                this.application.configure(
                        new File(this.dummyPropertiesFilePath)));
    }

    @Test
    public void configureSaveConfigurationWhenOkayed() throws Exception {
        setUpDefaultExpectations();
        this.mockPresenter.expects(once()).method(eq("getReturnStatus"))
                .will(returnValue(ConfigurationPresenter.RETURN_OK));
        this.mockConfiguration.expects(once()).method("saveConfiguration")
                .with(isA(OutputStream.class));
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    @Test
    public void configureDisplaysView() throws Exception {
        setUpDefaultExpectations();
        this.mockPresenter.expects(once()).method(eq("initialiseView"));
        this.mockPresenter.expects(once()).method(eq("realiseView"));
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    @Test(expected=ApplicationException.class)
    public void configureWhenLoadConfigurationFails() throws Exception {
        setUpDefaultExpectations();
        File propertiesFile = new File(this.dummyPropertiesFilePath);
        propertiesFile.createNewFile();
        this.mockConfiguration.expects(once()).method("loadConfiguration")
                .with(isA(InputStream.class))
                .will(throwException(new IOException()));
        this.mockPresenter.expects(never()).method(eq("getReturnStatus"));
        this.mockConfiguration.expects(never()).method("saveConfiguration");
        this.application.configure(propertiesFile);
    }

    @Test(expected=ApplicationException.class)
    public void testConfigureWhenSaveConfigurationFails() throws Exception {
        setUpDefaultExpectations();
        this.mockPresenter.expects(once()).method(eq("getReturnStatus"))
                .will(returnValue(ConfigurationPresenter.RETURN_OK));
        this.mockConfiguration.expects(once()).method("saveConfiguration")
                .with(isA(OutputStream.class))
                .will(throwException(new IOException()));
        this.application.configure(new File(this.dummyPropertiesFilePath));
    }

    private class StubAbstractApplication extends AbstractApplication {

        StubAbstractApplication(Configuration configuration) {
            super(configuration);
        }

        /**
         * {@inheritDoc}
         */
        protected ConfigurationPresenter createConfigurationPresenter() {
            return (ConfigurationPresenter) AbstractApplicationTest.this
                    .mockPresenter.proxy();
        }

        /**
         * {@inheritDoc}
         */
        protected ConfigurationView createConfigurationView() {
            return (ConfigurationView) AbstractApplicationTest.this
                    .mockView.proxy();
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            AssertMo.notImplemented("StubAbstractApplication");
        }
    }
}
