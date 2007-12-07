/*
 * ServerConfigurationDialog.java
 *
 * 07 Jun 2007
 */

package suncertify.presentation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import suncertify.ApplicationMode;

/**
 * Configuration dialog for applications running in
 * {@link ApplicationMode#SERVER SERVER} mode.
 * 
 * @author Richard Wardle
 */
public final class ServerConfigurationDialog extends
        AbstractConfigurationDialog {

    private JTextField databaseFilePathField;
    private JSpinner serverPortSpinner;
    private JButton browseButton;

    /**
     * Creates a new instance of <code>ServerConfigurationDialog</code>.
     */
    public ServerConfigurationDialog() {
        setTitle(getResourceBundle().getString(
                "ServerConfigurationDialog.title"));
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerConfigurationDialog.this.getPresenter()
                        .browseButtonActionPerformed();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    protected String getMessageText() {
        return getResourceBundle().getString(
                "ServerConfigurationDialog.message");
    }

    /** {@inheritDoc} */
    @Override
    public String getDatabaseFilePath() {
        return databaseFilePathField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setDatabaseFilePath(String databaseFilePath) {
        databaseFilePathField.setText(databaseFilePath);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getServerPort() {
        return (Integer) serverPortSpinner.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setServerPort(Integer serverPort) {
        if (serverPort == null) {
            throw new IllegalArgumentException("serverPort cannot be null");
        }
        serverPortSpinner.setValue(serverPort);
    }

    /** {@inheritDoc} */
    @Override
    protected JPanel initialiseInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        panel.add(new JLabel(getResourceBundle().getString(
                "ServerConfigurationDialog.databaseFilePathLabel.text")),
                constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        constraints.weightx = 1;
        databaseFilePathField = new JTextField();
        databaseFilePathField.setEditable(false);
        databaseFilePathField.setToolTipText(getResourceBundle().getString(
                "ServerConfigurationDialog.databaseFilePathTextField.tooltip"));
        panel.add(databaseFilePathField, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        browseButton = new JButton(getResourceBundle().getString(
                "ServerConfigurationDialog.browseButton.text"));
        browseButton.setMnemonic(getResourceBundle().getString(
                "ServerConfigurationDialog.browseButton.mnemonic").charAt(0));
        panel.add(browseButton, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;
        panel
                .add(new JLabel(getResourceBundle().getString(
                        "ServerConfigurationDialog.serverPortLabel.text")),
                        constraints);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets = PresentationConstants.DEFAULT_INSETS;

        serverPortSpinner = createServerPortSpinner(databaseFilePathField
                .getFont());
        panel.add(serverPortSpinner, constraints);

        return panel;
    }
}
