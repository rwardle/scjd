package suncertify;

import java.util.Properties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import suncertify.presentation.ServerConfigurationDialog;

@RunWith(TestClassRunner.class)
public class ServerApplicationTest {

    private ServerApplication application;

    @Before
    public void setUp() {
        this.application = new ServerApplication(new Configuration(
                new Properties()));
    }

    @Test
    public void createConfigurationView() {
        Assert.assertTrue(
                this.application.createConfigurationView() instanceof ServerConfigurationDialog);
    }
}
