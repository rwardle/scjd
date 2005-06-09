/*
 * AbstractConfigurationDialog.java
 *
 * Created on 05-Jun-2005
 */


package suncertify.startup;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * @author Richard Wardle
 */
public abstract class AbstractConfigurationDialog implements ConfigurationView {
    
    private static Logger logger = Logger
            .getLogger(AbstractConfigurationDialog.class.getName());
    
    private JPanel buttonPanel;
    private JButton cancelButton;
    private JDialog dialog;
    private JLabel messageLabel;
    private JPanel messagePanel;
    private JButton okButton;
    private String databaseFilePath;
    private String serverAddress;
    private String serverPort;
    
    /**
     * Creates a new AbstractConfigurationDialog.
     */
    public AbstractConfigurationDialog() {
        this.dialog = new JDialog(new JFrame(), true);
    }
    
    /**
     * {@inheritDoc}
     */
    public void addCancelButtonListener(ActionListener listener) {
        this.cancelButton.addActionListener(listener);
    }
    
    /**
     * {@inheritDoc}
     */
    public void addOkButtonListener(ActionListener listener) {
        this.okButton.addActionListener(listener);
    }
    
    /**
     * {@inheritDoc}
     */
    public void close() {
        this.dialog.setVisible(false);
        this.dialog.dispose();
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
    public String getServerAddress() {
        return this.serverAddress;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getServerPort() {
        return this.serverPort;
    }
    
    /**
     * {@inheritDoc}
     */
    public void packAndShow() {
        this.dialog.pack();
        this.dialog.setVisible(true);
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
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }
    
    /**
     * @return
     */
    protected abstract JPanel getInputPanel();
    
    /**
     * @return
     */
    protected abstract String getMessageText();
    
    public void initialiseComponents() {
        this.messagePanel = new JPanel();
        this.dialog.getContentPane().add(this.messagePanel, BorderLayout.NORTH);
        
        // TODO: this needs centering properly
        this.dialog.setLocationRelativeTo(null);
        
        this.messageLabel = new JLabel();
        this.messageLabel.setText(getMessageText());
        this.messagePanel.add(this.messageLabel);
        
        this.dialog.getContentPane().add(getInputPanel(), BorderLayout.CENTER);
        
        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        this.dialog.getContentPane().add(this.buttonPanel, BorderLayout.SOUTH);
        
        this.okButton = new JButton();
        this.okButton.setName("configurationDialog.okButton");
        this.okButton.setText("OK");
        this.buttonPanel.add(this.okButton);
        
        this.cancelButton = new JButton();
        this.cancelButton.setName("configurationDialog.cancelButton");
        this.cancelButton.setText("Cancel");
        this.buttonPanel.add(this.cancelButton);
    }
    
    protected void setOkEnabled() {
        this.okButton.setEnabled(true);
    }
    
    protected void setOkDisabled() {
        this.okButton.setEnabled(false);
    }
    
    protected boolean isOkEnabled() {
        return this.okButton.isEnabled();
    }
}
