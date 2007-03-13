package suncertify;

import org.junit.Assert;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import suncertify.presentation.ClientConfigurationDialog;

public class ClientApplicationTest {
    
    private ClientApplication application;
    
    @Before
    public void setUp() {
        this.application = new ClientApplication(new Configuration(new Properties()));
    }
    
    @Test
    public void createConfigurationView() {
        Assert.assertTrue("Instance of ClientConfigurationDialog expected", this.application
                .createConfigurationView() instanceof ClientConfigurationDialog);
    }
}
