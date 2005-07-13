/*
 * ServerApplicationTest.java
 *
 * Created on 12-Jul-2005
 */


package suncertify;

import java.util.Properties;

import junit.framework.TestCase;

import suncertify.presentation.ServerConfigurationDialog;


/**
 * Unit tests for {@link suncertify.ServerApplication}.
 *
 * @author Richard Wardle
 */
public final class ServerApplicationTest extends TestCase {

    private ServerApplication application;

    /**
     * Creates a new instance of <code>ServerApplicationTest</code>.
     *
     * @param name The test case name.
     */
    public ServerApplicationTest(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() {
        this.application = new ServerApplication(new Configuration(
                new Properties()));
    }

    /**
     * Should return an instance of <code>ServerConfigurationDialog</code>.
     */
    public void testCreateConfigurationView() {
        assertTrue("Instance of ServerConfigurationDialog expected",
                this.application.createConfigurationView()
                        instanceof ServerConfigurationDialog);
    }
}
