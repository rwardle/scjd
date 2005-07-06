/*
 * MainTest.java
 *
 * Created on 05-Jul-2005
 */


package suncertify;

import java.util.logging.Logger;

import junit.framework.TestCase;


/**
 * Unit tests for {@link suncertify.Main}.
 *
 * @author Richard Wardle
 */
public final class MainTest extends TestCase {

    private static Logger logger = Logger.getLogger(MainTest.class.getName());

    /**
     * Creates a new instance of <code>MainTest</code>.
     *
     * @param name The test case name.
     */
    public MainTest(String name) {
        super(name);
    }

    /**
     * Tests {@link Main#getApplicationMode()} when the command-line arguments
     * are null.
     */
    public void testGetApplicationModeNullArgs() {
        try {
            Main main = new Main(null);
            main.getApplicationMode();
            fail("NullPointerException expected when the command line argument "
                    + "array is null");
        } catch (NullPointerException e) {
            MainTest.logger.info("Caught expected NullPointerException: "
                    + e.getMessage());
        }
    }

    /**
     * Tests {@link Main#getApplicationMode()} when the command-line argument is
     * invalid.
     */
    public void testGetApplicationModeInvalidArg() {
        try {
            Main main = new Main(new String[] {"invalid"});
            main.getApplicationMode();
            fail("IllegalArgumentException expected when the mode flag is "
                    + "invalid");
        } catch (IllegalArgumentException e) {
            MainTest.logger.info("Caught expected "
                    + "IllegalArgumentException: " + e.getMessage());
        }
    }

    /**
     * Tests {@link Main#getApplicationMode()} when the there are no
     * command-line arguments.
     */
    public void testGetApplicationModeNoArgs() {
        Main main = new Main(new String[0]);
        assertEquals("Mode comparison,", ApplicationMode.CLIENT,
                main.getApplicationMode());
    }

    /**
     * Tests {@link Main#getApplicationMode()} when the command-line argument is
     * "server".
     */
    public void testGetApplicationModeServerArg() {
        Main main = new Main(new String[] {"server"});
        assertEquals("Mode comparison,", ApplicationMode.SERVER,
                main.getApplicationMode());
    }

    /**
     * Tests {@link Main#getApplicationMode()} when the command-line argument is
     * "alone".
     */
    public void testStandalonerModeFlag() {
        Main main = new Main(new String[] {"alone"});
        assertEquals("Mode comparison,", ApplicationMode.STANDALONE,
                main.getApplicationMode());
    }
}
