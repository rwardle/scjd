/*
 * ConfigurationViewFactoryTest.java
 *
 * Created on 08 June 2005
 */


package suncertify.startup;

import junit.framework.TestCase;


/**
 * Unit tests for {@link ConfigurationViewFactory}.
 *
 * @author Richard Wardle
 */
public final class ConfigurationViewFactoryTest extends TestCase {

    /**
     * Creates a new instance of ConfigurationViewFactoryTest.
     *
     * @param name The test case name.
     */
    public ConfigurationViewFactoryTest(String name) {
        super(name);
    }

    /**
     * Tests
     * {@link ConfigurationViewFactory#createConfigurationView(ApplicationMode}
     * with the client application mode.
     */
    public void testCreateClientConfigurationView() {
        assertTrue(
                "Factory should create an instance of "
                        + "ClientConfigurationDialog when the mode argument is "
                        + "'" + ApplicationMode.CLIENT + "'",
                ConfigurationViewFactory
                        .createConfigurationView(ApplicationMode.CLIENT)
                                instanceof ClientConfigurationDialog);
    }

    /**
     * Tests
     * {@link ConfigurationViewFactory#createConfigurationView(ApplicationMode}
     * with the server application mode.
     */
    public void testCreateServerConfigurationView() {
        assertTrue(
                "Factory should create an instance of "
                        + "ServerConfigurationDialog when the mode argument "
                        + "is '" + ApplicationMode.SERVER + "'",
                ConfigurationViewFactory
                        .createConfigurationView(ApplicationMode.SERVER)
                                instanceof ServerConfigurationDialog);
    }

    /**
     * Tests
     * {@link ConfigurationViewFactory#createConfigurationView(ApplicationMode}
     * with the standalone application mode.
     */
    public void testCreateStandaloneConfigurationView() {
        assertTrue(
                "Factory should create an instance of "
                        + "StandaloneConfigurationDialog when the mode "
                        + "argument is '" + ApplicationMode.STANDALONE + "'",
                ConfigurationViewFactory
                        .createConfigurationView(ApplicationMode.STANDALONE)
                                instanceof StandaloneConfigurationDialog);
    }
}
