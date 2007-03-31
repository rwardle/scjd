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
import suncertify.presentation.ClientConfigurationDialog;
import suncertify.presentation.ConfigurationPresenter;

public class ClientConfigurationFixture extends Fixture {

    private final Configuration configuration;
    private final GuiTest guiTest;
    private ConfigurationPresenter presenter;

    public ClientConfigurationFixture() {
        this.configuration = new Configuration(
                new Properties(ApplicationConstants.DEFAULT_PROPERTIES));
        this.guiTest = new GuiTest();
        this.guiTest.initialise();
    }

    public void delay(int delay) {
        Robot.setAutoDelay(delay);
    }

    public void serverAddress(String address) {
        new JTextComponentTester().actionEnterText(
                this.guiTest.getServerAddressField(), address);
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

    public String serverAddressValue() {
        return this.configuration.getServerAddress();
    }

    public String serverPortValue() {
        return this.configuration.getServerPort();
    }

    public String returnStatusValue() {
        return Integer.toString(this.presenter.getReturnStatus());
    }


    private class GuiTest extends ComponentTestFixture {

        private JTextField serverPortField;
        private JTextField serverAddressField;
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

        JTextField getServerAddressField() {
            return this.serverAddressField;
        }

        JTextField getServerPortField() {
            return this.serverPortField;
        }

        void initialise() {
            try {
                fixtureSetUp();

                ClientConfigurationDialog dialog =
                        new ClientConfigurationDialog();
                ClientConfigurationFixture.this.presenter =
                        new ConfigurationPresenter(
                                ClientConfigurationFixture.this
                                        .configuration,
                                dialog);

                showModalDialog(new Runnable() {
                    public void run() {
                        ClientConfigurationFixture.this.presenter
                                .realiseView();
                    }
                });

                this.serverAddressField = (JTextField) getFinder().find(
                        new NameMatcher("ClientConfigurationDialog"
                                + ".serverAddressField"));

                this.serverPortField = (JTextField) getFinder().find(
                        new NameMatcher("ClientConfigurationDialog"
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
