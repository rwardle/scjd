/*
 * BrokerServiceLocatorTest.java
 *
 * Created on 11 June 2005
 */


package suncertify.service;

import java.util.logging.Logger;

import junit.framework.TestCase;

import suncertify.startup.ApplicationMode;
import suncertify.startup.Configuration;


/**
 * Unit tests for {@link BrokerServiceLocator}.
 *
 * @author Richard Wardle
 */
public final class BrokerServiceLocatorTest extends TestCase {

    private static Logger logger = Logger.getLogger(BrokerServiceLocatorTest
            .class.getName());

    private Configuration configuration;

    /**
     * Creates a new instance of BrokerServiceLocatorTest.
     *
     * @param name The test case name.
     */
    public BrokerServiceLocatorTest(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() {
        this.configuration = new Configuration("dummy.properties");
    }

    /**
     * Tests {@link BrokerServiceLocator#getBrokerService(ApplicationMode,
     * Configuration)} with a null configuration.
     */
    public void testGetBrokerServiceNullConfiguration() {
        try {
            BrokerServiceLocator.getBrokerService(ApplicationMode.CLIENT,
                    null);
            fail("NullPointerException expected when method is called with a "
                    + "null configuration argument");
        } catch (NullPointerException e) {
            BrokerServiceLocatorTest.logger.info(
                    "Caught expected NullPointerException: " + e.getMessage());
        }
    }

    /**
     * Tests {@link BrokerServiceLocator#getBrokerService(ApplicationMode,
     * Configuration)} with the standalone application mode.
     */
    public void testGetStandaloneBrokerService() {
        BrokerService service = BrokerServiceLocator
                .getBrokerService(ApplicationMode.STANDALONE,
                        this.configuration);
        assertTrue(
                "Locator should return an instance of BrokerServiceImpl when "
                        + "the mode argument is '" + ApplicationMode.STANDALONE
                        + "'",
                service instanceof BrokerServiceImpl);
    }

    /**
     * Tests {@link BrokerServiceLocator#getBrokerService(ApplicationMode,
     * Configuration)} with the server application mode.
     */
    public void testGetServerBrokerService() {
        try {
            BrokerServiceLocator.getBrokerService(ApplicationMode.SERVER,
                    this.configuration);
            fail("UnsupportedOperationException expected when the method is "
                    + "invoked with a mode of '" + ApplicationMode.SERVER
                    + "'");
        } catch (UnsupportedOperationException e) {
            BrokerServiceLocatorTest.logger.info(
                    "Caught expected UnsupportedOperationException:"
                    + e.getMessage());
        }
    }
}
