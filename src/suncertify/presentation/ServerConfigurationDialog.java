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
import javax.swing.SpinnerNumberModel;

import suncertify.ApplicationConstants;

/**
 * Server mode configuration dialog.
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
        this.browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerConfigurationDialog.this.getPresenter()
                        .browseButtonActionPerformed();
            }
        });
    }

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
    public String getDatabaseFilePath() {
        return this.databaseFilePathField.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDatabaseFilePath(String databaseFilePath) {
        this.databaseFilePathField.setText(databaseFilePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getServerPort() {
        return (Integer) this.serverPortSpinner.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServerPort(Integer serverPort) {
        this.serverPortSpinner.setValue(serverPort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPanel initialiseInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;
        panel.add(new JLabel(getResourceBundle().getString(
                "ServerConfigurationDialog.databaseFilePathLabel.text")),
                constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;
        constraints.weightx = 1;
        this.databaseFilePathField = new JTextField();
        this.databaseFilePathField.setEditable(false);
        this.databaseFilePathField
                .setToolTipText(getResourceBundle()
                        .getString(
                                "ServerConfigurationDialog.databaseFilePathField.tooltip"));
        panel.add(this.databaseFilePathField, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;
        this.browseButton = new JButton(getResourceBundle().getString(
                "ServerConfigurationDialog.browseButton.text"));
        this.browseButton.setMnemonic(getResourceBundle().getString(
                "ServerConfigurationDialog.browseButton.mnemonic").charAt(0));
        panel.add(this.browseButton, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;
        panel
                .add(new JLabel(getResourceBundle().getString(
                        "ServerConfigurationDialog.serverPortLabel.text")),
                        constraints);

        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets = ApplicationConstants.DEFAULT_INSETS;

        this.serverPortSpinner = new JSpinner(new SpinnerNumberModel(
                AbstractConfigurationDialog.SERVER_PORT_SPINNER_INITIAL_VALUE,
                AbstractConfigurationDialog.SERVER_PORT_SPINNER_MINIMUM_VALUE,
                AbstractConfigurationDialog.SERVER_PORT_SPINNER_MAXIMUM_VALUE,
                AbstractConfigurationDialog.SERVER_PORT_SPINNER_STEP_SIZE));
        this.serverPortSpinner.setFont(this.databaseFilePathField.getFont());
        this.serverPortSpinner.setToolTipText(getResourceBundle().getString(
                "ServerConfigurationDialog.serverPortSpinner.tooltip"));
        JSpinner.DefaultEditor spinnerEditor = new JSpinner.NumberEditor(
                this.serverPortSpinner,
                AbstractConfigurationDialog.SERVER_PORT_SPINNER_FORMAT_PATTERN);
        spinnerEditor.getTextField().setColumns(
                AbstractConfigurationDialog.SERVER_PORT_SPINNER_COLUMNS);
        this.serverPortSpinner.setEditor(spinnerEditor);
        panel.add(this.serverPortSpinner, constraints);

        return panel;
    }
}
