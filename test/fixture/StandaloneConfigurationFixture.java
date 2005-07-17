/*
 * StandaloneConfigurationFixture.java
 *
 * Created on 17-Jul-2005
 */


package fixture;

import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JTextField;

import junit.extensions.abbot.ComponentTestFixture;

import abbot.finder.matchers.NameMatcher;
import abbot.tester.ComponentTester;
import abbot.tester.JTextComponentTester;
import abbot.tester.Robot;

import fit.Fixture;

import suncertify.ApplicationConstants;
import suncertify.Configuration;
import suncertify.presentation.ConfigurationPresenter;
import suncertify.presentation.StandaloneConfigurationDialog;


/**
 * Fixture for standalone mode configuration acceptance tests.
 *
 * @author Richard Wardle
 */
public final class StandaloneConfigurationFixture extends Fixture {

    private Configuration configuration;
    private ConfigurationPresenter presenter;
    private GuiTest guiTest;

    /**
     * Creates a new instance of <code>StandaloneConfigurationFixture</code>.
     */
    public StandaloneConfigurationFixture() {
        this.configuration = new Configuration(
                new Properties(ApplicationConstants.DEFAULT_PROPERTIES));
        this.guiTest = new GuiTest();
        this.guiTest.initialise();
    }

    /**
     * Sets the robot delay.
     *
     * @param delay The delay.
     */
    public void delay(int delay) {
        Robot.setAutoDelay(delay);
    }

    /**
     * Enters the database file path.
     *
     * @param path The path.
     */
    public void databaseFilePath(String path) {
        new JTextComponentTester().actionEnterText(
                this.guiTest.getDatabaseFilePathField(), path);
    }

    /**
     * Clicks OK.
     */
    public void ok() {
        new ComponentTester().actionClick(this.guiTest.getOkButton());
    }

    /**
     * Clicks Cancel.
     */
    public void cancel() {
        new ComponentTester().actionClick(this.guiTest.getCancelButton());
    }

    /**
     * Gets the database file path value.
     *
     * @return The database file path.
     */
    public String databaseFilePathValue() {
        return this.configuration.getDatabaseFilePath();
    }

    /**
     * Gets the dialog return status.
     *
     * @return The return status.
     */
    public String returnStatusValue() {
        return Integer.toString(this.presenter.getReturnStatus());
    }


    private class GuiTest extends ComponentTestFixture {

        private JTextField databaseFilePathField;
        private JButton okButton;
        private JButton cancelButton;

        GuiTest() {
            super();
        }

        JButton getOkButton() {
            return this.okButton;
        }

        JButton getCancelButton() {
            return this.cancelButton;
        }

        JTextField getDatabaseFilePathField() {
            return this.databaseFilePathField;
        }

        void initialise() {
            try {
                fixtureSetUp();

                StandaloneConfigurationDialog dialog =
                        new StandaloneConfigurationDialog();
                StandaloneConfigurationFixture.this.presenter =
                        new ConfigurationPresenter(
                                StandaloneConfigurationFixture.this
                                        .configuration,
                                dialog);
                StandaloneConfigurationFixture.this.presenter.initialiseView();

                showModalDialog(new Runnable() {
                    public void run() {
                        StandaloneConfigurationFixture.this.presenter
                                .realiseView();
                    }
                });

                this.databaseFilePathField = (JTextField) getFinder().find(
                        new NameMatcher("StandaloneConfigurationDialog"
                                + ".databaseFilePathField"));

                this.okButton = (JButton) getFinder().find(new NameMatcher(
                        "AbstractConfigurationDialog.okButton"));

                this.cancelButton = (JButton) getFinder().find(new NameMatcher(
                        "AbstractConfigurationDialog.cancelButton"));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
