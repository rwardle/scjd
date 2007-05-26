package suncertify;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import suncertify.presentation.StandaloneConfigurationDialog;
import suncertify.service.BrokerServiceImpl;

@RunWith(TestClassRunner.class)
public class StandaloneApplicationTest {

    private final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private Configuration mockConfiguration;
    private StandaloneApplication application;

    @Before
    public void setUp() {
        this.mockConfiguration = this.context.mock(Configuration.class);
        this.application = new StandaloneApplication(this.mockConfiguration);
    }

    @After
    public void verify() {
        this.context.assertIsSatisfied();
    }

    @Test
    public void createConfigurationView() {
        Assert.assertTrue(
                this.application.createConfigurationView() instanceof StandaloneConfigurationDialog);
    }

    @Test
    public void getBrokerService() {
        this.context.checking(new Expectations() {{
            ignoring(StandaloneApplicationTest.this.mockConfiguration);
        }});
        Assert.assertTrue(
                this.application.getBrokerService() instanceof BrokerServiceImpl);
    }
}
