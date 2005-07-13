/*
 * StandaloneApplicationTest.java
 *
 * Created on 12-Jul-2005
 */


package suncertify;

import java.util.Properties;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import suncertify.presentation.StandaloneConfigurationDialog;
import suncertify.service.BrokerServiceImpl;


/**
 * Unit tests for {@link suncertify.StandaloneApplication}.
 *
 * @author Richard Wardle
 */
public final class StandaloneApplicationTest extends MockObjectTestCase {

    private StandaloneApplication application;
    private Mock mockConfiguration;

    /**
     * Creates a new instance of <code>StandaloneApplicationTest</code>.
     *
     */
    public StandaloneApplicationTest() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() {
        this.mockConfiguration = mock(Configuration.class,
                new Class[] {Properties.class},
                new Object[] {new Properties()});
        this.application = new StandaloneApplication(
                (Configuration) this.mockConfiguration.proxy());
    }

    /**
     * Should return an instance of <code>StandaloneConfigurationDialog</code>.
     */
    public void testCreateConfigurationView() {
        assertTrue("Instance of StandaloneConfigurationDialog expected",
                this.application.createConfigurationView()
                        instanceof StandaloneConfigurationDialog);
    }

    /**
     * Should return an instance of <code>BrokerServiceImpl</code>.
     */
    public void testGetBrokerService() {
        this.mockConfiguration.expects(once()).method("getDatabaseFilePath");
        assertTrue("Instance of BrokerServiceImpl expected",
                this.application.getBrokerService()
                        instanceof BrokerServiceImpl);
    }
}
