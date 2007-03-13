package suncertify;

import java.util.Properties;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import suncertify.presentation.StandaloneConfigurationDialog;
import suncertify.service.BrokerServiceImpl;

@RunWith(TestClassRunner.class)
public class StandaloneApplicationTest extends MockObjectTestCase {

    private StandaloneApplication application;
    private Mock mockConfiguration;

    @Before
    public void setUp() {
        this.mockConfiguration = mock(Configuration.class,
                new Class[] {Properties.class},
                new Object[] {new Properties()});
        this.application = new StandaloneApplication(
                (Configuration) this.mockConfiguration.proxy());
    }

    @After
    public void verify() {
        super.verify();
    }
    
    @Test
    public void createConfigurationView() {
        Assert.assertTrue("Instance of StandaloneConfigurationDialog expected",
                this.application.createConfigurationView()
                        instanceof StandaloneConfigurationDialog);
    }

    @Test
    public void getBrokerService() {
        this.mockConfiguration.expects(once()).method("getDatabaseFilePath");
        Assert.assertTrue("Instance of BrokerServiceImpl expected",
                this.application.getBrokerService()
                        instanceof BrokerServiceImpl);
    }
}
