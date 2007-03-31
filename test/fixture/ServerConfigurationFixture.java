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

public class ServerConfigurationFixture extends Fixture {

    private final Configuration configuration;
    private final GuiTest guiTest;
    private ConfigurationPresenter presenter;

    public ServerConfigurationFixture() {
        this.configuration = new Configuration(
                new Properties(ApplicationConstants.DEFAULT_PROPERTIES));
        this.guiTest = new GuiTest();
        this.guiTest.initialise();
    }

    public void delay(int delay) {
        Robot.setAutoDelay(delay);
    }

    public void databaseFilePath(String path) {
        new JTextComponentTester().actionEnterText(
                this.guiTest.getDatabaseFilePathField(), path);
    }

    public void serverPort(String port) {
        new JTextComponentTester().actionEnterText(
                this.guiTest.getServerPortField(), port);
    }

    public void ok() {
        new ComponentTester().actionClick(this.guiTest.getOkButton());
    }

    public void cancel() {
        new ComponentTester().actionClick(this.guiTest.getCancelButton());
    }

    public String databaseFilePathValue() {
        return this.configuration.getDatabaseFilePath();
    }

    public String serverPortValue() {
        return this.configuration.getServerPort();
    }

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
