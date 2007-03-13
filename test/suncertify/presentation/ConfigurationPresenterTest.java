package suncertify.presentation;

import java.awt.event.ActionListener;
import java.util.Properties;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import suncertify.Configuration;

@RunWith(TestClassRunner.class)
public class ConfigurationPresenterTest extends MockObjectTestCase {

    private ConfigurationPresenter presenter;
    private final String databaseFilePath = "databaseFilePath";
    private final String serverAddress = "serverAddress";
    private final String serverPort = "serverPort";
    private Mock mockConfiguration;
    private Mock mockView;

    @Before
    public void setUp() {
        this.mockConfiguration = mock(Configuration.class,
                new Class[] {Properties.class},
                new Object[] {new Properties()});
        this.mockView = mock(ConfigurationView.class);
        this.presenter = new ConfigurationPresenter(
                (Configuration) this.mockConfiguration.proxy(),
                (ConfigurationView) this.mockView.proxy());
    }
    
    @After
    public void verify() {
        super.verify();
    }

    @Test(expected=NullPointerException.class)
    public void constructorDisallowNullConfiguration() {
        new ConfigurationPresenter(null,
                (ConfigurationView) this.mockView.proxy());
    }

    @Test(expected=NullPointerException.class)
    public void constructorDisallowNullView() {
        new ConfigurationPresenter(
                (Configuration) this.mockConfiguration.proxy(), null);
    }

    @Test
    public void constructorReturnStatus() {
        Assert.assertEquals(
                "Return status comparison,",
                ConfigurationPresenter.RETURN_CANCEL,
                this.presenter.getReturnStatus());
    }

    @Test
    public void initialiseView() {
        this.mockView.expects(once()).method("initialiseComponents");
        this.mockView.expects(once()).method("addOkButtonListener")
                .with(isA(ActionListener.class));
        this.mockView.expects(once()).method("addCancelButtonListener")
                .with(isA(ActionListener.class));
        
        this.mockConfiguration.expects(once()).method("getDatabaseFilePath")
                .will(returnValue(this.databaseFilePath));
        this.mockConfiguration.expects(once()).method("getServerAddress")
                .will(returnValue(this.serverAddress));
        this.mockConfiguration.expects(once()).method("getServerPort")
                .will(returnValue(this.serverPort));

        
        this.mockView.expects(once()).method("setDatabaseFilePath")
                .with(eq(this.databaseFilePath));
        this.mockView.expects(once()).method("setServerAddress")
                .with(eq(this.serverAddress));
        this.mockView.expects(once()).method("setServerPort")
                .with(eq(this.serverPort));

        this.presenter.initialiseView();
    }

    @Test
    public void realiseView() {
        this.mockView.expects(once()).method("realise");
        this.presenter.realiseView();
    }

    @Test
    public void okButtonActionPerformed() {
        String newDatabaseFilePath = "newDatabaseFilePath";
        String newServerAddress = "newServerAddress";
        String newServerPort = "newServerPort";

        this.mockView.expects(once()).method("getDatabaseFilePath")
                .will(returnValue(newDatabaseFilePath));
        this.mockView.expects(once()).method("getServerAddress")
                .will(returnValue(newServerAddress));
        this.mockView.expects(once()).method("getServerPort")
                .will(returnValue(newServerPort));
        this.mockView.expects(once()).method("close");

        this.mockConfiguration.expects(once()).method("setDatabaseFilePath")
                .with(eq(newDatabaseFilePath));
        this.mockConfiguration.expects(once()).method("setServerAddress")
                .with(eq(newServerAddress));
        this.mockConfiguration.expects(once()).method("setServerPort")
                .with(eq(newServerPort));

        this.presenter.okButtonActionPerformed();
        Assert.assertEquals("Return status comparison",
                ConfigurationPresenter.RETURN_OK,
                this.presenter.getReturnStatus());
    }

    @Test
    public void cancelButtonActionPerformed() {
        this.mockView.expects(once()).method("close");
        this.presenter.cancelButtonActionPerformed();
        Assert.assertEquals("Return status comparison",
                ConfigurationPresenter.RETURN_CANCEL,
                this.presenter.getReturnStatus());
    }
}
