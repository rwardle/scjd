package suncertify;

import org.junit.Assert;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import suncertify.presentation.ClientConfigurationDialog;

@RunWith(TestClassRunner.class)
public class ClientApplicationTest {

    private ClientApplication application;

    @Before
    public void setUp() {
        this.application = new ClientApplication(new Configuration(
                new Properties()));
    }

    @Test
    public void createConfigurationView() {
        Assert.assertTrue(
                this.application.createConfigurationView() instanceof ClientConfigurationDialog);
    }
}
