/*
 * ServerConfigurationDialog.java
 *
 * Created on 07-Jun-2005
 */


package suncertify.startup;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * @author Richard Wardle
 */
public class ServerConfigurationDialog extends AbstractConfigurationDialog {

    private JPanel inputPanel;
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
    protected JPanel getInputPanel() {
        this.inputPanel = new JPanel(new GridLayout(2, 1));

        JPanel databaseFilePathPanel = new JPanel();
        this.inputPanel.add(databaseFilePathPanel);
                
        JLabel databaseFilePathLabel = new JLabel("Database file path:");
        databaseFilePathPanel.add(databaseFilePathLabel);
        this.databaseFilePathField = new JTextField();
        databaseFilePathPanel.add(this.databaseFilePathField);
        
        JPanel serverPortPanel = new JPanel();
        this.inputPanel.add(serverPortPanel);
                
        JLabel serverPortLabel = new JLabel("Server port:");
        serverPortPanel.add(serverPortLabel);
        this.serverPortField = new JTextField();
        serverPortPanel.add(this.serverPortField);
        
        return this.inputPanel;
    }

    /**
     * {@inheritDoc}
     */
    protected String getMessageText() {
        return "server";
    }

    public void setServerPort(String serverPort) {
        this.serverPortField.setText(serverPort);
    }

    public void setDatabaseFilePath(String databaseFilePath) {
        this.databaseFilePathField.setText(databaseFilePath);
    }

    public String getServerPort() {
        return this.serverPortField.getText();
    }

    public String getDatabaseFilePath() {
        return this.databaseFilePathField.getText();
    }

}
