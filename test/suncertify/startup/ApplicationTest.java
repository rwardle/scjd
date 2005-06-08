/*
 * ApplicationTest.java
 *
 * Created on 05-Jun-2005
 */


package suncertify.startup;

import java.io.IOException;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jmock.core.Verifiable;
import org.jmock.expectation.ExpectationCounter;
import org.jmock.util.Verifier;


/**
 * Unit tests for {@link suncertify.startup.Application}.
 *
 * @author Richard Wardle
 */
public class ApplicationTest extends TestCase {

    private static Logger logger = Logger.getLogger(ApplicationTest.class
            .getName());

    /**
     * Creates a new ApplicationTest.
     */
    public ApplicationTest() {
        super();
    }

    /**
     * Tests configure when the user OKs the dialog.
     */
    public void testConfigureOkayed() {
        MockApplication application = new MockApplication(
                ApplicationMode.STANDALONE);
        application.setConfigDialogCallsCount(1);
        application.setSaveErrorDialogCallsCount(0);

        MockConfiguration configuration = new MockConfiguration(
                "dummy.properties");
        configuration.setLoadCallsCount(1);
        configuration.setSaveCallsCount(1);

        assertTrue("Method should return true when user OKs", application
                .configure(configuration));
        application.verify();
        configuration.verify();
    }

    /**
     * Test configure when the user OKs the dialog but the proerties cannot be
     * saved.
     */
    public void testConfigureOkayedSaveFailed() {
        MockApplication application = new MockApplication(
                ApplicationMode.STANDALONE);
        application.setConfigDialogCallsCount(1);
        application.setSaveErrorDialogCallsCount(1);

        MockConfiguration configuration = new MockConfiguration(
                "dummy.properties") {
            public void saveConfiguration() throws IOException {
                super.saveConfiguration();
                throw new IOException();
            }
        };
        configuration.setLoadCallsCount(1);
        configuration.setSaveCallsCount(1);

        assertTrue("Method should return true when user OKs", application
                .configure(configuration));
        application.verify();
        configuration.verify();
    }

    /**
     * Tests configure when the user cancels the dialog.
     */
    public void testConfigureUserCancelled() {
        MockApplication application = new MockApplication(
                ApplicationMode.STANDALONE) {
            public int showConfigurationDialog(Configuration configuration) {
                super.showConfigurationDialog(configuration);
                return ConfigurationPresenter.RETURN_CANCEL;
            }
        };
        application.setConfigDialogCallsCount(1);
        application.setSaveErrorDialogCallsCount(0);

        MockConfiguration configuration = new MockConfiguration(
                "dummy.properties");
        configuration.setLoadCallsCount(1);
        configuration.setSaveCallsCount(0);

        assertFalse("Method should return false when user cancels", application
                .configure(configuration));
        application.verify();
        configuration.verify();
    }

    /**
     * Tests getting the mode from the command line arguments.
     */
    public void testGetModeFromCommandLine() {
        try {
            Application.getModeFromCommandLine(null);
            fail("Null command line arguments should give a "
                    + "NullPointerException");
        } catch (NullPointerException e) {
            logger.info("Caught expected NullPointerException: "
                    + e.getMessage());
        }

        assertEquals(
                "Zero length command line arguments should give CLIENT mode,",
                ApplicationMode.CLIENT, Application
                        .getModeFromCommandLine(new String[0]));
        assertEquals(
                "Command line arguments of 'server' should give SERVER mode,",
                ApplicationMode.SERVER, Application
                        .getModeFromCommandLine(new String[] {"server"}));

        assertEquals("Command line arguments of 'alone' should give "
                + "STANDALONE mode", ApplicationMode.STANDALONE, Application
                .getModeFromCommandLine(new String[] {"alone"}));

        try {
            Application.getModeFromCommandLine(new String[] {"garbage"});
            fail("Unrecognised first command line argument should give "
                    + "IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            logger.info("Caught expected IllegalArgumentException: "
                    + e.getMessage());
        }
    }

    private static class MockApplication extends Application implements
            Verifiable {

        private ExpectationCounter configDialogCounter = new ExpectationCounter(
                "configDialogCounter");
        private ExpectationCounter saveErrorDialogCounter
                = new ExpectationCounter("saveErrorDialogCounter");

        /**
         * @param mode
         */
        public MockApplication(ApplicationMode mode) {
            super(mode);
        }

        /**
         * {@inheritDoc}
         */
        public int showConfigurationDialog(Configuration configuration) {
            this.configDialogCounter.inc();
            return ConfigurationPresenter.RETURN_OK;
        }

        /**
         * {@inheritDoc}
         */
        public void showSaveConfigurationWarning(Configuration configuration) {
            this.saveErrorDialogCounter.inc();
        }

        /**
         * {@inheritDoc}
         */
        public void verify() {
            Verifier.verifyObject(this);
        }

        void setConfigDialogCallsCount(int count) {
            this.configDialogCounter.setExpected(count);
        }

        void setSaveErrorDialogCallsCount(int count) {
            this.saveErrorDialogCounter.setExpected(count);
        }
    }

    private static class MockConfiguration extends Configuration implements
            Verifiable {

        private ExpectationCounter loadCounter = new ExpectationCounter(
                "loadCounter");
        private ExpectationCounter saveCounter = new ExpectationCounter(
                "saveCounter");

        MockConfiguration(String propertiesFilePath) {
            super(propertiesFilePath);
        }

        /**
         * {@inheritDoc}
         */
        public boolean loadConfiguration() {
            this.loadCounter.inc();
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public void saveConfiguration() throws IOException {
            this.saveCounter.inc();
        }

        /**
         * {@inheritDoc}
         */
        public void verify() {
            Verifier.verifyObject(this);
        }

        void setLoadCallsCount(int count) {
            this.loadCounter.setExpected(count);
        }

        void setSaveCallsCount(int count) {
            this.saveCounter.setExpected(count);
        }
    }
}
