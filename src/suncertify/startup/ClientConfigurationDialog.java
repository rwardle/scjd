/*
 * ClientConfigurationDialog.java
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
public class ClientConfigurationDialog extends AbstractConfigurationDialog {

    private JPanel inputPanel;
    private JTextField serverAddressField;
    private JTextField serverPortField;
    
    /**
     * Creates a new ClientConfigurationDialog.
     */
    public ClientConfigurationDialog() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected JPanel getInputPanel() {
        this.inputPanel = new JPanel(new GridLayout(2, 1));

        JPanel serverAddressPanel = new JPanel();
        this.inputPanel.add(serverAddressPanel);
                
        JLabel serverAddressLabel = new JLabel("Server address:");
        serverAddressPanel.add(serverAddressLabel);
        this.serverAddressField = new JTextField();
        serverAddressPanel.add(this.serverAddressField);
        
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
        return "Client";
    }

    /**
     * {@inheritDoc}
     */
    public void setServerPort(String serverPort) {
        this.serverPortField.setText(serverPort);
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
    public String getServerAddress() {
        return this.serverAddressField.getText();
    }
}
