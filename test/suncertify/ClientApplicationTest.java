/*
 * ClientApplicationTest.java
 *
 * Created on 12-Jul-2005
 */


package suncertify;

import java.util.Properties;

import junit.framework.TestCase;

import suncertify.presentation.ClientConfigurationDialog;


/**
 * Unit tests for {@link suncertify.ClientApplication}.
 *
 * @author Richard Wardle
 */
public final class ClientApplicationTest extends TestCase {

    private ClientApplication application;

    /**
     * Creates a new instance of <code>ClientApplicationTest</code>.
     *
     * @param name The test case name.
     */
    public ClientApplicationTest(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() {
        this.application = new ClientApplication(new Configuration(
                new Properties()));
    }

    /**
     * Should return an instance of <code>ClientConfigurationDialog</code>.
     */
    public void testCreateConfigurationView() {
        assertTrue("Instance of ClientConfigurationDialog expected",
                this.application.createConfigurationView()
                        instanceof ClientConfigurationDialog);
    }
}
