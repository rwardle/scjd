/*
 * ClientConfigurationDialog.java
 *
 * Created on 07-Jun-2005
 */

package suncertify.presentation;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Client mode configuration dialog.
 *
 * @author Richard Wardle
 */
public final class ClientConfigurationDialog extends
        AbstractConfigurationDialog {

    private String databaseFilePath;
    private JTextField serverAddressField;
    private JTextField serverPortField;

    /**
     * {@inheritDoc}
     */
    protected String getMessageText() {
        return this.getResourceBundle().getString("ClientConfigurationDialog.message.text");
    }

    /**
     * {@inheritDoc}
     */
    protected void initInputPanel(JPanel inputPanel) {
        inputPanel.setLayout(new GridLayout(2, 1));

        JPanel serverAddressPanel = new JPanel();
        inputPanel.add(serverAddressPanel);

        JLabel serverAddressLabel = new JLabel(
                this.getResourceBundle().getString("ClientConfigurationDialog.serverAddressLabel.text"));
        serverAddressPanel.add(serverAddressLabel);
        this.serverAddressField = new JTextField();
        this.serverAddressField.setName(
                "ClientConfigurationDialog.serverAddressField.name");
        serverAddressPanel.add(this.serverAddressField);

        JPanel serverPortPanel = new JPanel();
        inputPanel.add(serverPortPanel);

        JLabel serverPortLabel = new JLabel(
                this.getResourceBundle().getString("ClientConfigurationDialog.serverPortLabel.text"));
        serverPortPanel.add(serverPortLabel);
        this.serverPortField = new JTextField();
        this.serverPortField.setName(
                "ClientConfigurationDialog.serverPortField.name");
        serverPortPanel.add(this.serverPortField);

        getContentPane().add(inputPanel, BorderLayout.CENTER);
    }

    /**
     * {@inheritDoc}
     */
    public String getDatabaseFilePath() {
        return this.databaseFilePath;
    }

    /**
     * {@inheritDoc}
     */
    public void setDatabaseFilePath(String databaseFilePath) {
        this.databaseFilePath = databaseFilePath;
    }

    /**
     * {@inheritDoc}
     */
    public String getServerAddress() {
        return this.serverAddressField.getText();
    }

    /**
     * {@inheritDoc}
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddressField.setText(serverAddress);
    }

    /**
     * {@inheritDoc}
     */
    public String getServerPort() {
        return this.serverPortField.getText();
    }

    /**
     * {@inheritDoc}
     */
    public void setServerPort(String serverPort) {
        this.serverPortField.setText(serverPort);
    }
}
