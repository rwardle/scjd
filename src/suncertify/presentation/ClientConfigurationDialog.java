/*
 * ClientConfigurationDialog.java
 *
 * Created on 07-Jun-2005
 */


package suncertify.presentation;

import java.awt.BorderLayout;
import java.awt.GridLayout;

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
     * Creates a new instance of <code>ClientConfigurationDialog</code>.
     */
    public ClientConfigurationDialog() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected String getMessageText() {
        // TODO: Add full text
        return "Client message.";
    }

    /**
     * {@inheritDoc}
     */
    protected void initialiseInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));

        JPanel serverAddressPanel = new JPanel();
        inputPanel.add(serverAddressPanel);

        JLabel serverAddressLabel = new JLabel("Server address:");
        serverAddressPanel.add(serverAddressLabel);
        this.serverAddressField = new JTextField();
        this.serverAddressField.setName(
                "ClientConfigurationDialog.serverAddressField");
        serverAddressPanel.add(this.serverAddressField);

        JPanel serverPortPanel = new JPanel();
        inputPanel.add(serverPortPanel);

        JLabel serverPortLabel = new JLabel("Server port:");
        serverPortPanel.add(serverPortLabel);
        this.serverPortField = new JTextField();
        this.serverPortField.setName(
                "ClientConfigurationDialog.serverPortField");
        serverPortPanel.add(this.serverPortField);

        getDialog().getContentPane().add(inputPanel, BorderLayout.CENTER);
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
