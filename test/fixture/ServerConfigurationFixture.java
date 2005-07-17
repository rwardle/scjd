/*
 * ServerConfigurationFixture.java
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
import suncertify.presentation.ServerConfigurationDialog;


/**
 * Fixture for server mode configuration acceptance tests.
 *
 * @author Richard Wardle
 */
public final class ServerConfigurationFixture extends Fixture {

    private Configuration configuration;
    private ConfigurationPresenter presenter;
    private GuiTest guiTest;

    /**
     * Creates a new instance of <code>ServerConfigurationFixture</code>.
     */
    public ServerConfigurationFixture() {
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
     * Enters the server port.
     *
     * @param port The server port.
     */
    public void serverPort(String port) {
        new JTextComponentTester().actionEnterText(
                this.guiTest.getServerPortField(), port);
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
     * Gets the server port value.
     *
     * @return The server port.
     */
    public String serverPortValue() {
        return this.configuration.getServerPort();
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
        private JTextField serverPortField;
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

        JTextField getServerPortField() {
            return this.serverPortField;
        }

        void initialise() {
            try {
                fixtureSetUp();

                ServerConfigurationDialog dialog =
                        new ServerConfigurationDialog();
                ServerConfigurationFixture.this.presenter =
                        new ConfigurationPresenter(
                                ServerConfigurationFixture.this
                                        .configuration,
                                dialog);
                ServerConfigurationFixture.this.presenter.initialiseView();

                showModalDialog(new Runnable() {
                    public void run() {
                        ServerConfigurationFixture.this.presenter
                                .realiseView();
                    }
                });

                this.databaseFilePathField = (JTextField) getFinder().find(
                        new NameMatcher("ServerConfigurationDialog"
                                + ".databaseFilePathField"));

                this.serverPortField = (JTextField) getFinder().find(
                        new NameMatcher("ServerConfigurationDialog"
                                + ".serverPortField"));

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
