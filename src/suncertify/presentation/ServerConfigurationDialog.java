/*
 * ServerConfigurationDialog.java
 *
 * 07 Jun 2007
 */

package suncertify.presentation;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Server mode configuration dialog.
 * 
 * @author Richard Wardle
 */
public final class ServerConfigurationDialog extends
        AbstractConfigurationDialog {

    private static final long serialVersionUID = 1L;
    private String serverAddress;
    private JTextField databaseFilePathField;
    private JTextField serverPortField;

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getMessageText() {
        return getResourceBundle().getString(
                "ServerConfigurationDialog.message.text");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initInputPanel(JPanel inputPanel) {
        inputPanel.setLayout(new GridLayout(2, 1));

        JPanel databaseFilePathPanel = new JPanel();
        inputPanel.add(databaseFilePathPanel);

        JLabel databaseFilePathLabel = new JLabel(getResourceBundle()
                .getString(
                        "ServerConfigurationDialog.databaseFilePathLabel.text"));
        databaseFilePathPanel.add(databaseFilePathLabel);
        this.databaseFilePathField = new JTextField();
        this.databaseFilePathField
                .setName("ServerConfigurationDialog.databaseFilePathField.name");
        databaseFilePathPanel.add(this.databaseFilePathField);

        JPanel serverPortPanel = new JPanel();
        inputPanel.add(serverPortPanel);

        JLabel serverPortLabel = new JLabel(getResourceBundle().getString(
                "ServerConfigurationDialog.serverPortLabel.text"));
        serverPortPanel.add(serverPortLabel);
        this.serverPortField = new JTextField();
        this.serverPortField
                .setName("ServerConfigurationDialog.serverPortField.name");
        serverPortPanel.add(this.serverPortField);

        getContentPane().add(inputPanel, BorderLayout.CENTER);
    }

    /**
     * {@inheritDoc}
     */
    public String getServerAddress() {
        return this.serverAddress;
    }

    /**
     * {@inheritDoc}
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * {@inheritDoc}
     */
    public String getDatabaseFilePath() {
        return this.databaseFilePathField.getText();
    }

    /**
     * {@inheritDoc}
     */
    public void setDatabaseFilePath(String databaseFilePath) {
        this.databaseFilePathField.setText(databaseFilePath);
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
