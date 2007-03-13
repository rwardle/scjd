package suncertify;

import java.util.Properties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import suncertify.presentation.ServerConfigurationDialog;

public class ServerApplicationTest {

    private ServerApplication application;

    @Before
    public void setUp() {
        this.application = new ServerApplication(new Configuration(
                new Properties()));
    }

    @Test
    public void createConfigurationView() {
        Assert.assertTrue("Instance of ServerConfigurationDialog expected",
                this.application.createConfigurationView()
                        instanceof ServerConfigurationDialog);
    }
}
