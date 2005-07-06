/*
 * ServerConfigurationDialog.java
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
 * Configuration dialog for server mode.
 *
 * @author Richard Wardle
 */
public final class ServerConfigurationDialog extends
        AbstractConfigurationDialog {

    private String serverAddress;
    private JTextField databaseFilePathField;
    private JTextField serverPortField;

    /**
     * Creates a new ServerConfigurationDialog.
     */
    public ServerConfigurationDialog() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected String getMessageText() {
        // TODO: Add full text
        return "Server message.";
    }

    /**
     * {@inheritDoc}
     */
    protected void initialiseInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));

        JPanel databaseFilePathPanel = new JPanel();
        inputPanel.add(databaseFilePathPanel);

        JLabel databaseFilePathLabel = new JLabel("Database file path:");
        databaseFilePathPanel.add(databaseFilePathLabel);
        this.databaseFilePathField = new JTextField();
        databaseFilePathPanel.add(this.databaseFilePathField);

        JPanel serverPortPanel = new JPanel();
        inputPanel.add(serverPortPanel);

        JLabel serverPortLabel = new JLabel("Server port:");
        serverPortPanel.add(serverPortLabel);
        this.serverPortField = new JTextField();
        serverPortPanel.add(this.serverPortField);

        getDialog().getContentPane().add(inputPanel, BorderLayout.CENTER);
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
