/*
 * ApplicationTest.java
 *
 * Created on 05-Jun-2005
 */


package suncertify.startup;

import java.util.logging.Logger;

import junit.framework.TestCase;


/**
 * Unit tests for {@link suncertify.startup.Application}.
 *
 * @author Richard Wardle
 */
public final class ApplicationTest extends TestCase {

    // TODO: Add tests for configure method

    private static Logger logger = Logger.getLogger(ApplicationTest.class
            .getName());

    /**
     * Creates a new instance of ApplicationTest.
     *
     * @param name The test case name.
     */
    public ApplicationTest(String name) {
        super(name);
    }

    /**
     * Tests {@link Application#Application(String[])} with a null array
     * argument.
     */
    public void testNullCommandLineArgumentArray() {
        try {
            new Application(null);
            fail("NullPointerException expected when the command line argument "
                    + "array is null");
        } catch (NullPointerException e) {
            ApplicationTest.logger.info("Caught expected NullPointerException: "
                    + e.getMessage());
        }
    }

    /**
     * Tests {@link Application#Application(String[])} with an invalid mode
     * flag.
     */
    public void testInvalidModeFlag() {
        try {
            new Application(new String[] {"invalid"});
            fail("IllegalArgumentException expected when the mode flag is "
                    + "invalid");
        } catch (IllegalArgumentException e) {
            ApplicationTest.logger.info("Caught expected "
                    + "IllegalArgumentException: " + e.getMessage());
        }
    }

    /**
     * Tests {@link Application#Application(String[])} without a mode flag.
     */
    public void testNoModeFlag() {
        Application application = new Application(new String[0]);
        assertEquals(
                "Mode should be '" + ApplicationMode.CLIENT
                        + "' when there is no mode flag,",
                ApplicationMode.CLIENT, application.getMode());
    }

    /**
     * Tests {@link Application#Application(String[])} with the server mode
     * flag.
     */
    public void testServerModeFlag() {
        Application application = new Application(new String[] {"server"});
        assertEquals(
                "Mode should be '" + ApplicationMode.SERVER
                        + "' when there is no mode flag,",
                ApplicationMode.SERVER, application.getMode());
    }

    /**
     * Tests {@link Application#Application(String[])} with the standalone mode
     * flag.
     */
    public void testStandalonerModeFlag() {
        Application application = new Application(new String[] {"alone"});
        assertEquals(
                "Mode should be '" + ApplicationMode.STANDALONE
                        + "' when there is no mode flag,",
                ApplicationMode.STANDALONE, application.getMode());
    }
}
